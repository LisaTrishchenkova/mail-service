package com.example.mailservice.dto.request;

import com.example.mailservice.dto.enums.CodePurpose;

import java.io.Serializable;

public record SendCodeMessage(
        String to,
        CodePurpose purpose
) implements Serializable {
}
