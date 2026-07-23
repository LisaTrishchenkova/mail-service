package com.example.mailservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Единый формат ошибки")
public record ApiError(
    @Schema(description = "Время возникновения ошибки") Instant timestamp,
    @Schema(description = "HTTP-статус", example = "400") int status,
    @Schema(description = "Описание ошибки", example = "Ошибка валидации") String message,
    @Schema(
            description = "Ошибки по конкретным полям (для валидации)",
            example = "{\"to\": \"Некорректный email\"}")
        Map<String, String> fieldErrors) {}
