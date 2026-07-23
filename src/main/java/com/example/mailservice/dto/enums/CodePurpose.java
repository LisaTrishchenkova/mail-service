package com.example.mailservice.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        """
        Назначение кода подтверждения:
        * REGISTRATION — подтверждение регистрации
        * PASSWORD_RESET — восстановление пароля
        * LOGIN — подтверждение входа
        * EMAIL_CHANGE — смена адреса почты
        """)
public enum CodePurpose {
  REGISTRATION("Подтверждение регистрации", "Завершить регистрацию"),
  PASSWORD_RESET("Восстановление пароля", "Сбросить пароль"),
  LOGIN("Подтверждение входа", "Подтвергить вход в аккаунт"),
  EMAIL_CHANGE("Смена адреса почты", "Подтвердить смену адреса");

  private final String thema;
  private final String action;

  CodePurpose(String thema, String action) {
    this.thema = thema;
    this.action = action;
  }

  public String thema() {
    return thema;
  }

  public String action() {
    return action;
  }
}
