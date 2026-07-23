package com.example.mailservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI mailServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Mail Service API")
                .description(
                    """
                                Микросервис для отправки электронной почты через Gmail SMTP.

                                Возможности:
                                - отправка произвольного письма;
                                - генерация и отправка 6-значного кода подтверждения \\
                                с готовым HTML-оформлением под разные сценарии.
                                """)
                .version("1.0.0")
                .contact(new Contact().name("Lisa").email("chernenko-elizaveta@gmail.com")));
  }
}
