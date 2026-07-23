package com.example.mailservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат отправки письма")
public record SendMailResponse(
    @Schema(description = "Признак успеха", example = "true") boolean status,
    @Schema(description = "Пояснительное сообщение", example = "Письмо принято в обработку")
        String message) {}
