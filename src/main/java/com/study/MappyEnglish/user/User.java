package com.study.MappyEnglish.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // 'user'는 PostgreSQL 예약어일 수 있으므로 'users' 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails { // UserDetails 상속 (Spring Security 필수)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // 해시된 비밀번호 저장

    // (선택) 사용자 이름
    @Column(nullable = false)
    private String username;

    @Builder
    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    // === UserDetails 인터페이스 구현 ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // (간단하게 'ROLE_USER' 권한 부여)
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email; // Spring Security에서 'username'으로 사용할 필드 (email 사용)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}