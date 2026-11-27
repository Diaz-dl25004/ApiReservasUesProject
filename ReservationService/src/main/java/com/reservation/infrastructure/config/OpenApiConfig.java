package com.reservation.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API de Reservas para Restaurante",
        version = "1.0",
        description = "Gestión de clientes, mesas y reservas por fecha y turno."))
public class OpenApiConfig {
}
