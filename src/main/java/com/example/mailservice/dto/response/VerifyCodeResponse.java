package com.example.mailservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат проверки кода подтверждения")
public record VerifyCodeResponse(
    @Schema(description = "Код верный и срок его жизни не истек", example = "true") boolean valid,
    @Schema(description = "Пояснение", example = "Код подтвержден") String message) {}
