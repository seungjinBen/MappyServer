package com.study.MappyEnglish.app.controller;
import com.study.MappyEnglish.app.dto.SearchResponseDto;
import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.entity.Place;
import com.study.MappyEnglish.app.repository.ConversationRepository;
import com.study.MappyEnglish.app.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceRepository repo;
    private final ConversationRepository conversationRepository;

    @GetMapping
    public List<Place> findAll() {
        return repo.findAll();  // pgAdmin4에 있던 데이터가 JSON으로 그대로 나감
    }

    @GetMapping("/{id}")
    public Place findOne(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponseDto> search(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(new SearchResponseDto(List.of(), List.of()));
        }

        // 1. 장소 검색 (이름만)
        List<Place> places = repo.findByNameContainingIgnoreCase(query);

        // 2. 대화 검색 (한국어 해석)
        List<Conversation> conversations = conversationRepository.findByKoreanText1ContainingIgnoreCase(query);

        return ResponseEntity.ok(new SearchResponseDto(places, conversations));
    }
}
