
package com.market.backend_market.uslugi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${market.mail.from}")
    private String from;

    @Value("${market.frontend.reset-url}")
    private String resetUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String email,
            String token
    ) {
        String link = resetUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Market - resetowanie hasla");

        message.setText("""
                Otrzymalismy prosbe o zresetowanie hasla.

                Aby ustawic nowe haslo, otworz link:

                %s

                Link wygasa po 15 minutach.

                Jesli nie prosiles o reset hasla,
                zignoruj te wiadomosc.
                """.formatted(link));

        mailSender.send(message);
    }
}
