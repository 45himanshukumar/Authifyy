package com.himanshu.Authifyy.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Async
    public  void sendWelcomeEmail(String toEmail,String name){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("welcome to our plateform");
        message.setText("Hello"+name+",\n\n Thanks for register with us!\n\nRegards,\nAuthify team");
        mailSender.send(message);
    }
    public void sendResetOtpEmail(String toEmail,String otp){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Otp");
        message.setText("Your Otp for Resetting Your Password is "+ otp + " Used this Otp for proceed With resetting your password");
        mailSender.send(message);
    }
    public void sendOtpEmail(String toEmail,String otp){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Account Varification Otp");
        message.setText("Your Otp is  "+otp+  "Varify Your Account Using This Otp");
        mailSender.send(message);
    }

}
