package com.evandro.e_commerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .description("Spring Boot E-Commerce REST API with JPA and Email Notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Evandro Giacomelli")
                                .email("evandro@example.com")));
    }
}