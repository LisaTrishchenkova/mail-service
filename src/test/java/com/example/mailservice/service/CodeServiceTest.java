package com.example.mailservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.mailservice.dto.enums.CodePurpose;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {
  @Mock private StringRedisTemplate redisTemplate;
  @Mock private ValueOperations<String, String> valueOperations;
  private CodeService codeService;

  @BeforeEach
  void setUp() {
    codeService = new CodeService(redisTemplate);
  }

  @Test
  @DisplayName("Should generate 6-digit code for registration")
  void generateAndStore_returnsSixDigitCode() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    String code = codeService.generateAndStore("user@example.com", CodePurpose.REGISTRATION);

    assertThat(code).hasSize(6);
    assertThat(code).matches("\\d{6}");
  }

  @Test
  void generateAndStore_savesCodeToRedisWithTtl() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    codeService.generateAndStore("user@example.com", CodePurpose.REGISTRATION);

    verify(valueOperations)
        .set(
            eq("verification:REGISTRATION:user@example.com"),
            anyString(),
            eq(Duration.ofMinutes(5)));
  }

  @Test
  void verify_returnsTrue_whenCodeMatches() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get("verification:REGISTRATION:user@example.com")).thenReturn("123456");

    boolean result = codeService.verify("user@example.com", CodePurpose.REGISTRATION, "123456");

    assertThat(result).isTrue();
    verify(redisTemplate).delete("verification:REGISTRATION:user@example.com");
  }

  @Test
  void verify_returnsFalse_whenCodeDoesNotMatch() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(anyString())).thenReturn("123456");

    boolean result = codeService.verify("user@example.com", CodePurpose.REGISTRATION, "000000");

    assertThat(result).isFalse();
  }

  @Test
  void verify_returnsFalse_whenCodeExpiredOrMissing() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(anyString())).thenReturn(null);

    boolean result = codeService.verify("user@example.com", CodePurpose.REGISTRATION, "123456");

    assertThat(result).isFalse();
  }
}
