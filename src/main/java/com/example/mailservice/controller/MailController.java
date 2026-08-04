package com.example.mailservice.controller;

import com.example.mailservice.dto.enums.CodePurpose;
import com.example.mailservice.dto.request.SendCodeMessage;
import com.example.mailservice.dto.request.SendMailRequest;
import com.example.mailservice.dto.response.ApiError;
import com.example.mailservice.dto.response.SendCodeResponse;
import com.example.mailservice.dto.response.SendMailResponse;
import com.example.mailservice.dto.response.VerifyCodeResponse;
import com.example.mailservice.entity.EmailLog;
import com.example.mailservice.service.CodeService;
import com.example.mailservice.service.MailQueueProducer;
import com.example.mailservice.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@Validated
@Tag(name = "Mail", description = "Отправка писем и кодов подтверждения")
public class MailController {
  private final MailService mailService;
  private final CodeService codeService;
  private final MailQueueProducer mailQueueProducer;

    public MailController(MailService mailService, CodeService codeService, MailQueueProducer mailQueueProducer) {
    this.mailService = mailService;
    this.codeService = codeService;
    this.mailQueueProducer = mailQueueProducer;
  }

  @Operation(
      summary = "Отправить произвольное письмо",
      description = "Отправляет простое текстовое письмо на указанный адрес.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Письмо принято в обработку"),
    @ApiResponse(
        responseCode = "400",
        description = "Ошибка валидации входных данных",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Не удалось отправить письмо через SMTP",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping("/send")
  public ResponseEntity<SendMailResponse> send(@Valid @RequestBody SendMailRequest request) {
    mailService.send(request);
    return ResponseEntity.ok(new SendMailResponse(true, "Письмо принято в обработку!"));
  }

  @Operation(
      summary = "Отправить код подтверждения",
      description =
          """
                    Генерирует случайный 6-значный код и отправляет его красиво \
                    оформленным HTML-письмом. Тема и текст письма подставляются \
                    автоматически в зависимости от выбранного назначения (purpose).
                    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Код успешно отправлен"),
    @ApiResponse(
        responseCode = "400",
        description = "Неккоретный адрес или назначение",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "502",
        description = "Не удалось отправить письмо через SMTP",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping("/send-code")
  public ResponseEntity<SendCodeResponse> sendCode(
      @Parameter(description = "Email получателя", example = "user@example.com")
          @RequestParam
          @NotBlank
          @Email
          String to,
      @Parameter(description = "Назначение кода - определяет тему и текст письма") @RequestParam
          CodePurpose purpose) {
    mailService.sendCode(to, purpose);
    return ResponseEntity.ok(new SendCodeResponse(true, "Код отправлен на " + to));
  }

  @Operation(
      summary = "История отправленных писем",
      description = "Возвращает список всех попыток отправки (новые сверху) с их статусом.")
  @GetMapping("/logs")
  public List<EmailLog> logs() {
    return mailService.getLogs();
  }

  @Operation(
      summary = "Проверить код подтверждения",
      description = "Сверяет введённый код с сохранённым в Redis. Код одноразовый и живёт 5 минут.")
  @PostMapping("/verify-code")
  public ResponseEntity<VerifyCodeResponse> verifyCode(
      @Parameter(description = "Email, на который отправляли код", example = "user@example.com")
          @RequestParam
          @NotBlank
          @Email
          String to,
      @Parameter(description = "Назначение кода (должно совпадать с тем, что при отправке)")
          @RequestParam
          CodePurpose purpose,
      @Parameter(description = "6-значный код из письма", example = "123456")
          @RequestParam
          @NotBlank
          String code) {

    boolean valid = codeService.verify(to, purpose, code);
    if (valid) {
      return ResponseEntity.ok(new VerifyCodeResponse(true, "Код подтверждён"));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new VerifyCodeResponse(false, "Неверный или просроченный код"));
  }
  @Operation(
          summary = "Отправить код подтверждения асинхронно (через очередь)",
          description = "Кладёт задачу в очередь RabbitMQ и сразу отвечает. " + "Письмо отправит консьюмер в фоне."
    )
  @PostMapping("/send-code-async")
  public ResponseEntity<SendCodeResponse> sendCodeAsync(
          @Parameter(description = "Email получателя", example = "user@example.com")
          @RequestParam @NotBlank @Email String to,
          @Parameter(description = "Назначение кода")
          @RequestParam CodePurpose purpose) {
        mailQueueProducer.sendCodeToQueue(new SendCodeMessage(to, purpose));
        return ResponseEntity.ok(
                new SendCodeResponse(true, "Задача принята в обработку (очередь)")
        );
    }
}
