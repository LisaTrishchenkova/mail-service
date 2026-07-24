# Mail Service

Микросервис для отправки электронной почты через Gmail SMTP: произвольные письма
и 6-значные коды подтверждения с готовым HTML-оформлением. Написан на Spring Boot,
работает в Docker вместе с PostgreSQL и Redis.

## Возможности

- Отправка произвольного письма (получатель, тема, текст) по HTTP.
- Генерация и отправка 6-значного кода подтверждения красивым HTML-письмом
  под разные сценарии (регистрация, сброс пароля, вход, смена почты).
- Проверка кода (одноразовый, живёт 5 минут) с хранением в Redis.
- Логирование всех попыток отправки в PostgreSQL (статус SENT / FAILED).
- Валидация входных данных и единый формат ошибок.
- Интерактивная документация API через Swagger UI.
- Health-check через Spring Actuator.

## Стек технологий

- Java 21, Spring Boot 4.1
- Spring Web, Spring Data JPA, Spring Data Redis, Spring Mail
- PostgreSQL (хранение логов), Redis (коды с TTL)
- springdoc OpenAPI (Swagger UI)
- Docker, Docker Compose
- JUnit 5, Mockito, JaCoCo (тесты и покрытие)

## Быстрый старт

Требуется установленный Docker и Docker Compose.

1. Скопируй шаблон переменных окружения и заполни своими значениями:
```bash
   cp .env.example .env
```
   В `.env` укажи Gmail-адрес и App Password (см. раздел «Конфигурация»).

2. Собери приложение и образ:
```bash
   ./mvnw clean package -DskipTests
   docker build -t mail-service:local .
```

3. Подними всю систему (приложение + PostgreSQL + Redis):
```bash
   docker compose up -d
```

4. Открой Swagger UI: http://localhost:8080/swagger-ui

## Конфигурация

Все настройки задаются через переменные окружения (файл `.env`):

| Переменная        | Описание                          | Пример                    |
|-------------------|-----------------------------------|---------------------------|
| `MAIL_USERNAME`   | Gmail-адрес отправителя           | `you@gmail.com`           |
| `MAIL_PASSWORD`   | Gmail App Password (без пробелов) | `abcdefghijklmnop`        |
| `POSTGRES_DB`     | Имя базы данных                   | `mailservice`             |
| `POSTGRES_USER`   | Пользователь БД                   | `mailuser`                |
| `POSTGRES_PASSWORD` | Пароль БД                       | `mailpass`                |

> **App Password** создаётся в настройках Google-аккаунта при включённой
> двухэтапной аутентификации: https://myaccount.google.com/apppasswords

## API

| Метод | Путь                     | Описание                          |
|-------|--------------------------|-----------------------------------|
| POST  | `/api/mail/send`         | Отправить произвольное письмо      |
| POST  | `/api/mail/send-code`    | Отправить 6-значный код            |
| POST  | `/api/mail/verify-code`  | Проверить код                      |
| GET   | `/api/mail/logs`         | История отправок                   |

Подробности и возможность попробовать — в Swagger UI (`/swagger-ui`).

## Тесты

```bash
./mvnw test
```

Отчёт о покрытии (JaCoCo) после `./mvnw verify`:
`target/site/jacoco/index.html`
