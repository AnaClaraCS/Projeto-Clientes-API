package com.projeto.email_cliente_api.componentes;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.email_cliente_api.config.RabbitMQConfig;
import com.projeto.email_cliente_api.dtos.ClienteRequest;
import com.projeto.email_cliente_api.services.EmailService;

@Component
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public RabbitMQConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        try {
            ClienteRequest cliente = objectMapper.readValue(message, ClienteRequest.class);
            emailService.enviarEmailConfirmacao(cliente);
            System.out.println("Email enviado para: " + cliente.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
