package com.study.MappyEnglish.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자를 찾는 메서드
    Optional<User> findByEmail(String email);

    // 이메일 중복 검사용
    boolean existsByEmail(String email);
}