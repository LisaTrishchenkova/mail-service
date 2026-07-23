package com.example.mailservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "email_log")
public class EmailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String recipient;

  @Column(nullable = false)
  private String theme;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmailStatus status;

  @Column(columnDefinition = "text")
  private String errorMessage;

  @Column(nullable = false)
  private Instant createdAt;

  protected EmailLog() {}

  public EmailLog(String recipient, String theme, EmailStatus status, String errorMessage) {
    this.recipient = recipient;
    this.theme = theme;
    this.status = status;
    this.errorMessage = errorMessage;
    this.createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getSubject() {
    return theme;
  }

  public EmailStatus getStatus() {
    return status;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
