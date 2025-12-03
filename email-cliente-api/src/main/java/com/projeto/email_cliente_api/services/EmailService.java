package com.projeto.email_cliente_api.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.projeto.email_cliente_api.dtos.ClienteRequest;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailConfirmacao(ClienteRequest cliente) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(cliente.getEmail());
        message.setSubject("Cadastro realizado com sucesso!");
        message.setText("Olá " + cliente.getNome() +
                ", seu cadastro foi efetuado com sucesso em nossa API!");

        System.out.println("\n\n\n\n\n\nEnviando email para: " + cliente.getEmail());
        System.out.println(message.getText());

        mailSender.send(message);
    }
}

