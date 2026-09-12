package com.jobify.mail;

import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class SmtpMailSenderFactory {

    private final MailProperties mailProperties;

    public SmtpMailSenderFactory(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    public JavaMailSender create(String username, String password) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");
        if (mailProperties.getProperties() != null && !mailProperties.getProperties().isEmpty()) {
            Properties props = new Properties();
            props.putAll(mailProperties.getProperties());
            sender.setJavaMailProperties(props);
        }
        return sender;
    }
}
