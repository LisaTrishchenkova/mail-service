package com.example.mailservice.dto.request;

import com.example.mailservice.dto.enums.CodePurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendCodeRequest(
    @NotBlank @Email String to,
    @NotNull(message = "Укажите значение кода (purpose)") CodePurpose purpose) {}
