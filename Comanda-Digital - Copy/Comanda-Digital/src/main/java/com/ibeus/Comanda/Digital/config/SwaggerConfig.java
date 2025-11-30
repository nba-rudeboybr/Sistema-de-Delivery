package com.ibeus.Comanda.Digital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Comanda Digital API")
                        .version("1.0.0")
                        .description("API para sistema de gerenciamento de comandas digitais de restaurantes")
                        .contact(new Contact()
                                .name("iBeus")
                                .email("contato@ibeus.com")
                                .url("https://ibeus.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

