package com.study.MappyEnglish.app.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct; // Spring Boot 3 기준
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final String bucketName;
    private final String serviceAccountKeyBase64;
    private GoogleCredentials credentials;

    public FirebaseStorageService(
            @Value("${firebase.storage-bucket}") String bucketName,
            @Value("${firebase.service-account-key:}") String serviceAccountKeyBase64) {
        this.bucketName = bucketName;
        this.serviceAccountKeyBase64 = serviceAccountKeyBase64;
    }

    // 스프링 부트가 켜질 때 한 번만 실행되어 Firebase 열쇠를 꽂고 준비하는 과정
    @PostConstruct
    public void initialize() {
        try {
            credentials = GoogleCredentials.fromStream(openServiceAccountKey());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setStorageBucket(bucketName)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.err.println("Firebase 초기화 실패: " + e.getMessage());
        }
    }

    // 운영: 환경변수 FIREBASE_SERVICE_ACCOUNT_KEY (base64) / 로컬: resources/firebase-key.json
    private InputStream openServiceAccountKey() throws IOException {
        if (serviceAccountKeyBase64 != null && !serviceAccountKeyBase64.isBlank()) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(serviceAccountKeyBase64.trim()));
        }
        return new ClassPathResource("firebase-key.json").getInputStream();
    }

    // mp3 byte 배열을 받아서 Firebase에 올리고 URL을 반환
    public String uploadAudio(byte[] audioBytes) {
        try {
            // 안 겹치도록 랜덤한 파일 이름 생성 (예: tts/5329f8a...mp3)
            String fileName = "tts/" + UUID.randomUUID().toString() + ".mp3";

            // Storage 접속 준비 (초기화 때 만든 자격 증명 재사용)
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

            // 파일 정보 세팅 (오디오 파일이라고 명시)
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType("audio/mpeg")
                    .build();

            // Firebase에 업로드 발사
            storage.create(blobInfo, audioBytes);

            return "gs://" + bucketName + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
