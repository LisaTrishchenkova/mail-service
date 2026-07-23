package com.example.mailservice.repository;

import com.example.mailservice.entity.EmailLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

  List<EmailLog> findAllByOrderByCreatedAtDesc();
}
