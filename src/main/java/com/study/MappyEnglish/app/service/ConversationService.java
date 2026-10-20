package com.study.MappyEnglish.app.service;

import com.study.MappyEnglish.app.dto.ConversationDto;
import com.study.MappyEnglish.app.dto.ConversationCreateDto;
import com.study.MappyEnglish.app.dto.ConversationMapper;
import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.entity.Place;
import com.study.MappyEnglish.app.repository.ConversationRepository;
import com.study.MappyEnglish.app.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository repo;
    private final PlaceRepository placeRepo;
    private final AiService aiService;
    private final FirebaseStorageService firebaseService;

    public ConversationService(ConversationRepository repo, PlaceRepository placeRepo,
                               AiService aiService, FirebaseStorageService firebaseService) {
        this.repo = repo;
        this.placeRepo = placeRepo;
        this.aiService = aiService;
        this.firebaseService = firebaseService;
    }

    @Transactional(readOnly = true)
    public ConversationDto getById(Long id) {
        Conversation e = repo.findByIdWithPlace(id)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + id));
        return ConversationMapper.toDto(e);
    }

    @Transactional(readOnly = true)
    public List<ConversationDto> listByPlace(Long placeId, boolean includePlace) {
        List<Conversation> list = includePlace
                ? repo.findAllByPlaceIdWithPlace(placeId)
                : repo.findAllByPlaceId(placeId);
        return list.stream().map(ConversationMapper::toDto).toList();
    }

    public List<Conversation> findByCategory(String category) {
        return repo.findByCategory(category);
    }

    public List<Conversation> getConversationsBySection(String section) {
        return repo.findBySection(section);
    }
    @Transactional
    public void saveConversation(ConversationCreateDto dto) {
        Place place = placeRepo.findById(dto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        // 1. 해당 장소에 이미 등록된 대화 목록을 ID 오름차순(1단계, 2단계 순)으로 모두 불러옴
        List<Conversation> existingConvs = repo.findAllByPlaceIdOrderByIdAsc(dto.getPlaceId());

        Conversation conversation;
        int targetIndex = dto.getStep() - 1; 

        // 2. 기존 데이터가 존재하면 해당 객체를 가져와서 수정(UPDATE)하고, 없으면 새로 생성(INSERT)
        if (existingConvs != null && existingConvs.size() > targetIndex) {
            conversation = existingConvs.get(targetIndex);
        } else {
            conversation = new Conversation();
            conversation.setPlace(place);
        }

        // 타입 지정
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            conversation.setType(dto.getType().charAt(0));
        }

        // 카테고리(주제)가 넘어오면 DB에 저장
        if (dto.getCategory() != null) {
            conversation.setCategory(dto.getCategory());
        }

        // 텍스트가 수정되었는지 비교하기 위해, 덮어쓰기 전에 기존 데이터를 잠깐 기억해 둡니다.
        String oldEng1 = conversation.getEnglishText1(); String oldAud1 = conversation.getAudioUrl1();
        String oldEng2 = conversation.getEnglishText2(); String oldAud2 = conversation.getAudioUrl2();
        String oldEng3 = conversation.getEnglishText3(); String oldAud3 = conversation.getAudioUrl3();
        String oldEng4 = conversation.getEnglishText4(); String oldAud4 = conversation.getAudioUrl4();
        String oldEng5 = conversation.getEnglishText5(); String oldAud5 = conversation.getAudioUrl5();

        // 3. 이전 문장 데이터 초기화 (AI가 3문장만 만들었는데 기존에 5문장이 있었다면 찌꺼기가 남으므로 싹 비움)
        conversation.setEnglishText1(null); conversation.setKoreanText1(null); conversation.setAudioUrl1(null);
        conversation.setEnglishText2(null); conversation.setKoreanText2(null); conversation.setAudioUrl2(null);
        conversation.setEnglishText3(null); conversation.setKoreanText3(null); conversation.setAudioUrl3(null);
        conversation.setEnglishText4(null); conversation.setKoreanText4(null); conversation.setAudioUrl4(null);
        conversation.setEnglishText5(null); conversation.setKoreanText5(null); conversation.setAudioUrl5(null);

        // 타입 A: 내가 먼저 시작 (홀수 줄=나, 짝수 줄=상대방)
        // 타입 B: 상대방이 먼저 시작 (홀수 줄=상대방, 짝수 줄=나)
        boolean amIStarting = dto.getType() != null && dto.getType().equals("A");

        // 내가 말할 땐 남자(onyx), 상대가 말할 땐 여자(nova)
        String voiceForOdd = amIStarting ? "onyx" : "nova";  // 홀수 번째 줄(1, 3, 5) 목소리
        String voiceForEven = amIStarting ? "nova" : "onyx"; // 짝수 번째 줄(2, 4) 목소리

        // 4. 프론트에서 넘어온 배열 값으로 덮어쓰면서 스마트하게 TTS 생성
        List<ConversationCreateDto.LineDto> lines = dto.getLines();

        if (lines != null) {
            if (lines.size() > 0) {
                String newEng1 = lines.get(0).getEnglishText();
                conversation.setEnglishText1(newEng1);
                conversation.setKoreanText1(lines.get(0).getKoreanText());
                conversation.setAudioUrl1(smartGenerateAudio(newEng1, oldEng1, oldAud1, voiceForOdd)); // 홀수
            }
            if (lines.size() > 1) {
                String newEng2 = lines.get(1).getEnglishText();
                conversation.setEnglishText2(newEng2);
                conversation.setKoreanText2(lines.get(1).getKoreanText());
                conversation.setAudioUrl2(smartGenerateAudio(newEng2, oldEng2, oldAud2, voiceForEven)); // 짝수
            }
            if (lines.size() > 2) {
                String newEng3 = lines.get(2).getEnglishText();
                conversation.setEnglishText3(newEng3);
                conversation.setKoreanText3(lines.get(2).getKoreanText());
                conversation.setAudioUrl3(smartGenerateAudio(newEng3, oldEng3, oldAud3, voiceForOdd)); // 홀수
            }
            if (lines.size() > 3) {
                String newEng4 = lines.get(3).getEnglishText();
                conversation.setEnglishText4(newEng4);
                conversation.setKoreanText4(lines.get(3).getKoreanText());
                conversation.setAudioUrl4(smartGenerateAudio(newEng4, oldEng4, oldAud4, voiceForEven)); // 짝수
            }
            if (lines.size() > 4) {
                String newEng5 = lines.get(4).getEnglishText();
                conversation.setEnglishText5(newEng5);
                conversation.setKoreanText5(lines.get(4).getKoreanText());
                conversation.setAudioUrl5(smartGenerateAudio(newEng5, oldEng5, oldAud5, voiceForOdd)); // 홀수
            }
        }

        // 5. DB에 최종 저장 (JPA가 알아서 기존 데이터면 UPDATE, 새 데이터면 INSERT 쿼리를 실행)
        repo.save(conversation);
    }

    // 똑똑해진 헬퍼 메서드: 텍스트가 바뀌었을 때만 TTS를 새로 생성
    private String smartGenerateAudio(String newEnglishText, String oldEnglishText, String oldAudioUrl, String voice) {
        // 1. 텍스트가 아예 비어있으면 오디오도 없음
        if (newEnglishText == null || newEnglishText.trim().isEmpty()) {
            return null;
        }

        // 2. 핵심: 새로운 텍스트가 기존 텍스트와 완벽히 똑같고, 기존 오디오 URL이 이미 존재한다면?
        // -> 굳이 돈 써서 안 만들고 기존 URL 재사용! (정확성 + 비용 절약 동시 해결)
        if (newEnglishText.equals(oldEnglishText) && oldAudioUrl != null && !oldAudioUrl.isEmpty()) {
            return oldAudioUrl;
        }

        // 3. 텍스트가 처음 생겼거나, 관리자가 단어 하나라도 수정했다면? -> 무조건 OpenAI 호출해서 새 오디오 생성
        try {
            System.out.println("TTS 새로 생성 중... 목소리: " + voice + " / 텍스트: " + newEnglishText);
            // aiService에 텍스트와 함께 목소리 종류도 같이 넘겨줍니다!
            byte[] audioBytes = aiService.generateAudio(newEnglishText, voice);
            return firebaseService.uploadAudio(audioBytes);
        } catch (Exception e) {
            System.err.println("TTS 생성 및 업로드 실패: " + newEnglishText);
            e.printStackTrace();
            return null;
        }
    }
}