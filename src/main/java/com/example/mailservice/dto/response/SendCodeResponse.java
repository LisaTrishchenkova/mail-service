package com.example.mailservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат отправки кода подтверждения")
public record SendCodeResponse(
    @Schema(description = "Признак успеха", example = "true") boolean success,
    @Schema(description = "Пояснительное сообщение", example = "Код отправлен на user@example.com")
        String message) {}
