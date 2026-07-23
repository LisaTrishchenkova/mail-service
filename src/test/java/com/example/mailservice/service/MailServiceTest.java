package com.example.mailservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.mailservice.dto.request.SendMailRequest;
import com.example.mailservice.entity.EmailLog;
import com.example.mailservice.entity.EmailStatus;
import com.example.mailservice.repository.EmailLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
  @Mock private JavaMailSender mailSender;
  @Mock private EmailLogRepository emailLogRepository;
  @Mock private CodeService codeService;
  @Captor private ArgumentCaptor<EmailLog> emailLogCaptor;
  private MailService mailService;

  @BeforeEach
  void setUp() {
    mailService = new MailService(mailSender, emailLogRepository, codeService);
  }

  @Test
  void send_savesLogWithStatusSent_whenSuccessful() {

    SendMailRequest request = new SendMailRequest("user@example.com", "Тема", "Текст");
    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    mailService.send(request);

    verify(emailLogRepository).save(emailLogCaptor.capture());
    EmailLog saved = emailLogCaptor.getValue();
    assertThat(saved.getStatus()).isEqualTo(EmailStatus.SENT);
    assertThat(saved.getRecipient()).isEqualTo("user@example.com");
    assertThat(saved.getErrorMessage()).isNull();
  }

  @Test
  void send_savesLogWithStatusFailed_andRethrows_whenSmtpFails() {

    SendMailRequest request = new SendMailRequest("user@example.com", "Тема", "Текст");
    doThrow(new MailSendException("SMTP недоступен"))
        .when(mailSender)
        .send(any(SimpleMailMessage.class));

    assertThatThrownBy(() -> mailService.send(request)).isInstanceOf(MailSendException.class);

    verify(emailLogRepository).save(emailLogCaptor.capture());
    EmailLog saved = emailLogCaptor.getValue();
    assertThat(saved.getStatus()).isEqualTo(EmailStatus.FAILED);
    assertThat(saved.getRecipient()).isEqualTo("user@example.com");
    assertThat(saved.getErrorMessage()).contains("SMTP недоступен");
  }
}
