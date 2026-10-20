package com.study.MappyEnglish.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // (a) CORS 설정 통합
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // (b) CSRF 비활성화 (API 서버는 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // (c) 세션 비활성화 (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // (d) 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 1. [공개 API]: 로그인 없이 모두 접근 가능 ---
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()      // 로그인/회원가입
                        .requestMatchers("/api/places/**").permitAll()     // 장소 API
                        // (↓↓ 여기에 다른 '공개' API 경로를 추가하세요 ↓↓)
                        .requestMatchers("/api/conversations/**").permitAll()
                        .requestMatchers("/api/media/**").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll() // 예: 리뷰 '조회'는 공개
                        .requestMatchers("/api/ai/**", "/api/conversations/**", "/api/places/**").permitAll()
                        .requestMatchers("/api/stt/**").permitAll()
                        .requestMatchers("/api/contact/**").permitAll()
                        .requestMatchers("/api/ping/**").permitAll()

                        // 2. [보호된 API]: 로그인한 사용자만 접근 가능 ---
                        .requestMatchers("/api/my-cards/**").authenticated() // "나의 회화카드"
                        // .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated() // 예: 리뷰 '작성'은 인증
                        // 만약 관리자만 접근하게 하려면 .requestMatchers("/api/ai/**").hasRole("ADMIN") 처럼 설정할 수도 있습니다.
                        // 3. 나머지 모든 요청 ---
                        // 설정하지 않은 나머지 경로는 기본적으로 인증이 필요하도록 설정
                        .anyRequest().authenticated()
                )

                // (e) JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // (4) CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://mappyenglish.com",
                "https://www.mappyenglish.com"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}