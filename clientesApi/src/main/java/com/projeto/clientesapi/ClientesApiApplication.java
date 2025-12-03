package com.projeto.clientesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@SpringBootApplication(scanBasePackages = "com.projeto.clientesapi")
public class ClientesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientesApiApplication.class, args);
	}

}
