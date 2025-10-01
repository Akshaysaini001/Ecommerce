package com.akshay.ecommerce.service;
import com.akshay.ecommerce.entity.Product;
import com.akshay.ecommerce.entity.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${activation.base-url}")
    private String activationUrl;
    @Async
    public void sendActivationEmail(String toEmail, String token) {
        String link = activationUrl + "?token=" + token;
        String subject = "Activate your account";
        String body = "Please activate your account by clicking this link: " + link;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Async
    public void sendRegisterEmail(String toEmail, String companyName) {
        String subject = "Your account has been registered for company : "+ companyName;
        String body = "Waiting for approval. it would be done by admin";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Async
    public void sendPlainEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetUrl, int ttlMinutes) {
        String subject = "Reset your password";
        String body = "WE received a request to reset your password \n" + "This link is valid for " + ttlMinutes + " minutes:\n"
                + resetUrl ;
        sendPlainEmail(toEmail, subject, body);
    }
    @Async
    public void sendCustomerActivationEmail(String toEmail, String firstName) {
        String subject = "hello" + firstName +"Your Account Has Been Activated";
        String body = "You are all set";
        sendPlainEmail(toEmail, subject, body);
    }
    @Async
    public void sendCustomerDeactivationEmail(String toEmail, String firstName) {
        String subject = "hello" + firstName +"Your Account Has Been Deactivated";
        String body = "okay";
        sendPlainEmail(toEmail, subject, body);
    }
    @Async
    public void sendSellerActivationEmail(String toEmail, String firstName, String companyName) {
        String subject = "hello" + firstName +"Your Seller Account Has Been Activated - " + companyName;
        String body = "You are all set";
        sendPlainEmail(toEmail, subject, body);
    }

    @Async
    public void sendSellerDeactivationEmail(String toEmail, String firstName, String companyName) {
        String subject = "hello" + firstName +"Your Seller Account Has Been Deactivated - " + companyName;
        String body = "okay";
        sendPlainEmail(toEmail, subject, body);
    }
    @Async
    public void sendPasswordChangeEmail(String to) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Your password was changed");
        msg.setText("Password changed successfully");
        mailSender.send(msg);
    }

    @Async
    public void sendProductAdminNotification(String productName, String brand, String sellerCompany) {
        String subject = "New Product Added - " + productName;
        String body = "Product: " + productName + " " + brand + " added by " + sellerCompany;

        String adminEmail = "editzakshay137@gmail.com";
        sendPlainEmail(adminEmail, subject, body);
    }

     @Async
        public void sendProductActivationNotification(String sellerEmail, String productName, String brandName, String adminEmail) {
        String subject = "Product Activated - " + productName;
        String body = "Your product " + productName +  " has been Activated by admin ";
        sendPlainEmail(sellerEmail, subject, body);
        }


        @Async
        public void sendProductDeactivationNotification(String sellerEmail, String productName, String brandName, String adminEmail) {
            String subject = "Product Deactivated - " + productName;
            String body = "Your product " + productName +  " has been deactivated by admin ";
            sendPlainEmail(sellerEmail, subject, body);
        }
}




