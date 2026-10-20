package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.entity.Contact;
import com.study.MappyEnglish.app.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    @PostMapping("/send")
    public ResponseEntity<?> receiveContact(@RequestBody Contact contact) {
        try {
            contactRepository.save(contact); // DB에 저장
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패");
        }
    }
}
