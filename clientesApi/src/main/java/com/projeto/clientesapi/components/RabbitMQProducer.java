package com.projeto.clientesapi.components;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.clientesapi.events.ClienteCriado;

import org.springframework.amqp.core.Queue;

@Component
public class RabbitMQProducer {
    @Autowired RabbitTemplate rabbitTemplate;
    @Autowired ObjectMapper objectMapper;
    @Autowired Queue queue;
    
    public void send(ClienteCriado clienteCriado) {
        try {
            var json = objectMapper.writeValueAsString(clienteCriado); 
            rabbitTemplate.convertAndSend(queue.getName(), json); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
