package com.study.MappyEnglish.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.MappyEnglish.app.dto.SttResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class SttService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public SttService(WebClient.Builder webClientBuilder,
                      ObjectMapper objectMapper,
                      @Value("${openai.api-key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public SttResultDto evaluateText(String userText, String targetText) {

        int basicScore = calculateSimilarityScore(userText, targetText);

        if (basicScore >= 85) {
            return new SttResultDto(userText, targetText, 100, "완벽합니다! 훌륭한 타이핑이에요.");
        }

        return getAiEvaluation(userText, targetText);
    }

    public SttResultDto evaluatePronunciation(MultipartFile audioFile, String targetText) throws Exception {

        // 1. Whisper API로 오디오 -> 텍스트 변환
        ByteArrayResource fileResource = new ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() { return "audio.webm"; }
        };

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", fileResource, MediaType.valueOf("audio/webm"));
        bodyBuilder.part("model", "whisper-1");
        bodyBuilder.part("language", "en");

        Map transcriptionResponse = webClient.post()
                .uri("https://api.openai.com/v1/audio/transcriptions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String recognizedText = (String) transcriptionResponse.get("text");

        // 2. 하이브리드 로직: 1차 레벤슈타인 검사 (비용 & 속도 최적화)
        int basicScore = calculateSimilarityScore(recognizedText, targetText);

        if (basicScore >= 85) {
            // 유저가 정답과 거의 똑같이 말했다면, GPT 안 부르고 바로 100점 (API 비용 절감)
            return new SttResultDto(recognizedText, targetText, 100, "완벽합니다! 원어민 같네요.");
        }

        // 3. 점수가 낮다면 GPT 호출 (의미 채점 및 교정 피드백)
        return getAiEvaluation(recognizedText, targetText);
    }

    private SttResultDto getAiEvaluation(String recognized, String target) {
        String prompt = String.format(
                "You are an English teacher. Evaluate if the user's answer has the same meaning as the target sentence in a travel context.\n" +
                        "Target: \"%s\"\n" +
                        "User's Answer: \"%s\"\n\n" +
                        "Rules:\n" +
                        "1. If the meaning is substantially the same, give a score of 80-100.\n" +
                        "2. If it's partially correct but misses key info, give 40-70.\n" +
                        "3. If totally different, give 0-30.\n" +
                        "Return ONLY a JSON object in this format: {\"score\": number, \"feedback\": \"short Korean comment explaining what was good or what to improve\"}",
                target, recognized
        );

        try {
            // Map 대신 JsonNode를 쓰면 자바의 unchecked 경고가 사라짐
            JsonNode chatResponse = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(
                                    Map.of("role", "system", "content", "You are a helpful assistant that returns JSON."),
                                    Map.of("role", "user", "content", prompt)
                            ),
                            "response_format", Map.of("type", "json_object")
                    ))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            // JsonNode 트리에서 안전하게 content 문자열만 빼오기
            String content = chatResponse.get("choices").get(0).get("message").get("content").asText();

            GptEvalResponse eval = objectMapper.readValue(content, GptEvalResponse.class);

            return new SttResultDto(recognized, target, eval.score(), eval.feedback());

        } catch (Exception e) {
            System.err.println("GPT 파싱 중 에러 발생: " + e.getMessage());
            return new SttResultDto(recognized, target, 0, "평가 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    // Record 클래스
    private record GptEvalResponse(int score, String feedback) {}

    // 기존 레벤슈타인 로직 (1차 필터링용)
    private int calculateSimilarityScore(String recognized, String target) {
        if (recognized == null || target == null) return 0;
        String cleanRecognized = recognized.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim();
        String cleanTarget = target.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().trim();
        if (cleanRecognized.isEmpty() && !cleanTarget.isEmpty()) return 0;
        if (cleanRecognized.equals(cleanTarget)) return 100;
        int distance = computeLevenshteinDistance(cleanRecognized, cleanTarget);
        int maxLength = Math.max(cleanRecognized.length(), cleanTarget.length());
        double similarity = 1.0 - ((double) distance / maxLength);
        return (int) Math.round(similarity * 100);
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }
}