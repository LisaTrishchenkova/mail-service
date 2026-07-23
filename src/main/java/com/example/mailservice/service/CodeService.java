package com.example.mailservice.service;

import com.example.mailservice.dto.enums.CodePurpose;
import java.security.SecureRandom;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CodeService {
  private static final Duration CODE_TTL = Duration.ofMinutes(5);
  private final StringRedisTemplate redisTemplate;
  private final SecureRandom random = new SecureRandom();

  public CodeService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public String generateAndStore(String email, CodePurpose purpose) {
    String code = String.format("%06d", random.nextInt(1_000_000));
    redisTemplate.opsForValue().set(buildKey(email, purpose), code, CODE_TTL);
    return code;
  }

  public boolean verify(String email, CodePurpose purpose, String code) {
    String key = buildKey(email, purpose);
    String stored = redisTemplate.opsForValue().get(key);
    if (stored != null && stored.equals(code)) {
      redisTemplate.delete(key);
      return true;
    }
    return false;
  }

  private String buildKey(String email, CodePurpose purpose) {
    return "verification:" + purpose.name() + ":" + email;
  }
}
