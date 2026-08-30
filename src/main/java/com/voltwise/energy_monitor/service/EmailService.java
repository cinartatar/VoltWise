package com.voltwise.energy_monitor.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String receiver, String subject, String message){
        //send that mail
        SimpleMailMessage email= new SimpleMailMessage();
        email.setFrom("cinartatar2006@gmail.com");
        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(message);

        javaMailSender.send(email);

    }
}
