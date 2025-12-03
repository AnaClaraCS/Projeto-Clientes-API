package com.projeto.clientesapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
	
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
			.title("Clientes API")
			.version("v1")
			.description("Documentação da API de Cientes")
			.contact(new Contact()
			.name("Ana Souza")
			.email("ana.souza@sgp.uerj.br")));
		}
}