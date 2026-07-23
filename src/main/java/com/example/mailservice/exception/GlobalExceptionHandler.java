package com.example.mailservice.exception;

import com.example.mailservice.dto.response.ApiError;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handlerValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

    ApiError body =
        new ApiError(
            Instant.now(), HttpStatus.BAD_REQUEST.value(), "Ошибка валидации", fieldErrors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(MailException.class)
  public ResponseEntity<ApiError> handleMail(MailException ex) {
    ApiError body =
        new ApiError(
            Instant.now(),
            HttpStatus.BAD_GATEWAY.value(),
            "Не удалось отправить письмо: " + ex.getMessage(),
            null);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex) {
    ApiError body =
        new ApiError(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Внутренняя ошибка сервера",
            null);
    return ResponseEntity.internalServerError().body(body);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiError> handleParamValidation(HandlerMethodValidationException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getValueResults()
        .forEach(
            result -> {
              String paramName = result.getMethodParameter().getParameterName();
              result
                  .getResolvableErrors()
                  .forEach(err -> fieldErrors.put(paramName, err.getDefaultMessage()));
            });

    ApiError body =
        new ApiError(
            Instant.now(), HttpStatus.BAD_REQUEST.value(), "Ошибка валидации", fieldErrors);
    return ResponseEntity.badRequest().body(body);
  }
}
