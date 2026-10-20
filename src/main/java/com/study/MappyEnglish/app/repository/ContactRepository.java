package com.study.MappyEnglish.app.repository;
import com.study.MappyEnglish.app.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
