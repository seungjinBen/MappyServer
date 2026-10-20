package com.study.MappyEnglish.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.MappyEnglish.user.User;
import com.study.MappyEnglish.user.UserRepository;
import com.study.MappyEnglish.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 1. 전체 카카오 로그인 흐름 메서드
    public String kakaoLogin(String code) {

        // 1) 카카오 서버로 가서 Access Token 받아오기
        String kakaoAccessToken = getKakaoAccessToken(code);

        // 2) 받아온 Token으로 유저 이메일과 이름 빼오기
        JsonNode userInfo = getKakaoUserInfo(kakaoAccessToken);
        String kakaoId = userInfo.path("id").asText(); // 카카오 고유 식별 번호 (절대 안 변함!)
        String nickname = userInfo.path("properties").path("nickname").asText();

        String fetchedEmail = userInfo.path("kakao_account").path("email").asText();

        // 카카오가 이메일을 안 줬거나, 빈칸이거나, "null"이라는 글자로 오면 가짜 이메일 생성
        if (fetchedEmail == null || fetchedEmail.isBlank() || fetchedEmail.equals("null")) {
            fetchedEmail = "kakao_" + kakaoId + "@mappy.com";
        }

        final String validEmail = fetchedEmail;

        // 3) DB에 있는지 확인 후, 없으면 자동 회원가입
        User user = userRepository.findByEmail(validEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(validEmail)
                            .username(nickname)
                            .password("")
                            .authProvider("KAKAO")
                            .build();
                    return userRepository.save(newUser);
                });

        // 4) 스프링 시큐리티 Authentication 객체 생성 (권한 부여)
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
        );

        // 5) JwtTokenProvider를 사용해 토큰 발급 후 반환
        return jwtTokenProvider.generateToken(authentication);
    }

    // 2. 인가 코드로 카카오 Access Token 요청하기
    private String getKakaoAccessToken(String code) {
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(reqUrl, request, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 발급 실패", e);
        }
    }

    // 3. 카카오 Access Token으로 유저 정보 가져오기
    private JsonNode getKakaoUserInfo(String accessToken) {
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.GET, request, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("카카오 유저 정보 가져오기 실패", e);
        }
    }
}