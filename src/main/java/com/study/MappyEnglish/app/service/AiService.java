package com.study.MappyEnglish.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(@Value("${openai.api-key}") String apiKey) {
        // OpenAI와 통신할 WebClient 초기화 (헤더에 API 키 세팅)
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // 1. 텍스트 대화 생성 API 호출
    @SuppressWarnings("unchecked")
    public String generateDialogue(String placeName, String type) {
        String starter = type.equals("A") ? "관광객(나)" : "현지인 직원";

        // 1. 프롬프트: "반드시 JSON 객체(category와 lines) 형태로 대답해"
        String prompt = String.format(
                "너는 여행 영어 회화 전문가야. 장소는 '%s'이고, '%s'가 먼저 말을 시작해. " +
                        "이 상황에 맞는 자연스러운 영어 대화를 3~5번 주고받는(3~5문장) 형태로 만들어줘. " +
                        "추가로, 이 대화의 핵심 주제를 한국어로 한 줄로 요약해줘 (예: '크루아상 주문하기 및 수량 말하기'). " +
                        "반드시 아래 JSON 형식으로만 대답해: " +
                        "{\"category\": \"요약된 주제\", \"lines\": [{\"englishText\": \"...\", \"koreanText\": \"...\"}]}",
                placeName, starter
        );

        // 2. 시스템 세뇌 변경: outputs only JSON arrays -> outputs only JSON
        java.util.Map<String, Object> requestBody = java.util.Map.of(
                "model", "gpt-4o-mini",
                "messages", java.util.List.of(
                        java.util.Map.of("role", "system", "content", "You are a helpful assistant that outputs only JSON."),
                        java.util.Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        // API 호출 및 결과 받기 (WebClient 방식)
        java.util.Map response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .block();

        java.util.List<java.util.Map<String, Object>> choices = (java.util.List<java.util.Map<String, Object>>) response.get("choices");
        java.util.Map<String, Object> message = (java.util.Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    // 영어 텍스트를 OpenAI TTS를 이용해 mp3 byte 배열로 변환
    public byte[] generateAudio(String englishText, String voice) {
        // OpenAI TTS 요청 데이터 생성
        java.util.Map<String, Object> requestBody = java.util.Map.of(
                "model", "tts-1",
                "input", englishText,
                "voice", voice // nova 또는 onyx를 사용
        );

        // ✅ WebClient 방식에 맞춘 코드! (restClient -> webClient)
        return webClient.post()
                .uri("/audio/speech")
                .bodyValue(requestBody) // body() 대신 bodyValue() 사용
                .retrieve()
                .bodyToMono(byte[].class) // body() 대신 bodyToMono() 사용
                .block(); // 비동기 통신을 기다려주기 위해 block() 필수
    }
}
