package com.example.mailservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на отправку произвольного письма")
public record SendMailRequest(
    @Schema(description = "Email получателя", example = "user@example.com")
        @NotBlank(message = "Поле 'to' обязательно")
        @Email(message = "Некорректный email в поле 'to'")
        String to,
    @Schema(description = "Тема письма", example = "Тема письма!")
        @NotBlank(message = "Поле 'theme' обязательно")
        String theme,
    @Schema(description = "Текст письма", example = "Это тело письма.")
        @NotBlank(message = "Поле 'body' обязательно")
        String body) {}
