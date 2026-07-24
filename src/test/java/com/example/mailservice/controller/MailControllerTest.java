package com.example.mailservice.controller;

import com.example.mailservice.dto.enums.CodePurpose;
import com.example.mailservice.service.CodeService;
import com.example.mailservice.service.MailService;
import org.aspectj.apache.bcel.classfile.Code;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private CodeService codeService;
    @Test
    void send_returns200_whenRequestValid() throws Exception{
        String json = """
                {
                "to": "user@example.com",
                "theme": "Hello!",
                "body": "Text"
                }
                """;

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(mailService).send(any());
    }
    @Test
    void send_returns400_whenEmailInvalid() throws Exception{
        String json = """
                {
                "to": "user",
                "theme": "Hello!",
                "body": "Text"
                }
                """;

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(mailService, never()).send(any());
    }
    @Test
    void sendCode_returns200_whenParamsValid() throws Exception{
        mockMvc.perform(post("/api/mail/send-code")
                        .param("to", "user@example.com")
                        .param("purpose", "REGISTRATION"))
                .andExpect(status().isOk());

        verify(mailService).sendCode(eq("user@example.com"), eq(CodePurpose.REGISTRATION));
    }
}