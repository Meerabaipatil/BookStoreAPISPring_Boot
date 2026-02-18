package org.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // For OTP mails
    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("OTP for Password Reset");
        message.setText(
                "Dear User,\n\n" +
                "Your OTP for password reset is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Regards,\nAdmin Team"
        );
        mailSender.send(message);
    }

    // For normal text mails (suspend / block / reminder)
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
