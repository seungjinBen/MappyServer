package com.study.MappyEnglish.app.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct; // Spring Boot 3 기준
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final String BUCKET_NAME = "mappyproject-5ee09.firebasestorage.app";

    // 스프링 부트가 켜질 때 한 번만 실행되어 Firebase 열쇠를 꽂고 준비하는 과정
    @PostConstruct
    public void initialize() {
        try {
            // resources 폴더에 넣어둔 열쇠 파일 읽어오기
            InputStream serviceAccount = new ClassPathResource("firebase-key.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(BUCKET_NAME)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.err.println("Firebase 초기화 실패: " + e.getMessage());
        }
    }

    // mp3 byte 배열을 받아서 Firebase에 올리고 URL을 반환
    public String uploadAudio(byte[] audioBytes) {
        try {
            // 안 겹치도록 랜덤한 파일 이름 생성 (예: tts/5329f8a...mp3)
            String fileName = "tts/" + UUID.randomUUID().toString() + ".mp3";

            // Storage 접속 준비
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("firebase-key.json").getInputStream()))
                    .build()
                    .getService();

            // 파일 정보 세팅 (오디오 파일이라고 명시)
            BlobInfo blobInfo = BlobInfo.newBuilder(BUCKET_NAME, fileName)
                    .setContentType("audio/mpeg")
                    .build();

            // Firebase에 업로드 발사
            storage.create(blobInfo, audioBytes);

            return "gs://" + BUCKET_NAME + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}