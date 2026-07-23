package com.example.mailservice.service;

import com.example.mailservice.dto.enums.CodePurpose;
import com.example.mailservice.dto.request.SendMailRequest;
import com.example.mailservice.entity.EmailLog;
import com.example.mailservice.entity.EmailStatus;
import com.example.mailservice.repository.EmailLogRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {
  private final JavaMailSender mailSender;
  private final EmailLogRepository emailLogRepository;
  private final CodeService codeService;

  @Value("${MAIL_USERNAME:}")
  private String from;

  public MailService(
      JavaMailSender mailSender, EmailLogRepository emailLogRepository, CodeService codeService) {
    this.mailSender = mailSender;
    this.emailLogRepository = emailLogRepository;
    this.codeService = codeService;
  }

  public void send(SendMailRequest request) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(request.to());
    message.setSubject(request.theme());
    message.setText(request.body());

    sendAndLog(() -> mailSender.send(message), request.to(), request.theme());
  }

  public List<EmailLog> getLogs() {
    return emailLogRepository.findAllByOrderByCreatedAtDesc();
  }

  private void sendAndLog(Runnable sendAction, String to, String theme) {
    try {
      sendAction.run();
      emailLogRepository.save(new EmailLog(to, theme, EmailStatus.SENT, null));
    } catch (MailException ex) {
      emailLogRepository.save(new EmailLog(to, theme, EmailStatus.FAILED, ex.getMessage()));
      throw ex;
    }
  }

  public void sendCode(String to, CodePurpose purpose) {
    String code = codeService.generateAndStore(to, purpose);
    String html = buildCodeEmailHtml(code, purpose);

    MimeMessagePreparator preparator =
        mimeMessage -> {
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
          helper.setFrom(from);
          helper.setTo(to);
          helper.setSubject(purpose.thema());
          helper.setText(html, true);
        };

    mailSender.send(preparator);
  }

  private String buildCodeEmailHtml(String code, CodePurpose purpose) {
    return """
                 <!DOCTYPE html>
                            <html lang="ru">
                            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:Arial,Helvetica,sans-serif;">
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7; padding:40px 0;">
                                <tr>
                                  <td align="center">
                                    <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.06);">
                                      <tr>
                                        <td style="background-color:#4f46e5; padding:24px 32px;">
                                          <h1 style="margin:0; color:#ffffff; font-size:20px;">%s</h1>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style="padding:32px;">
                                          <p style="margin:0 0 16px; color:#333333; font-size:15px; line-height:1.5;">
                                            Здравствуйте! Чтобы %s, введите этот код подтверждения:
                                          </p>
                                          <div style="text-align:center; margin:24px 0;">
                                            <span style="display:inline-block; font-size:34px; letter-spacing:10px; font-weight:bold; color:#4f46e5; background-color:#eef2ff; padding:16px 24px; border-radius:8px;">%s</span>
                                          </div>
                                          <p style="margin:0; color:#888888; font-size:13px; line-height:1.5;">
                                            Код действителен ограниченное время. Если вы не запрашивали это письмо, просто проигнорируйте его.
                                          </p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style="background-color:#fafafa; padding:16px 32px; text-align:center;">
                                          <p style="margin:0; color:#aaaaaa; font-size:12px;">mail-service · автоматическое письмо</p>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </body>
                            </html>
        """
        .formatted(purpose.thema(), purpose.action(), code);
  }
}
