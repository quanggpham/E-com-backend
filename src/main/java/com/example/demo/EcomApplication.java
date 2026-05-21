package com.example.demo;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EcomApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(EcomApplication.class, args);
	}

	@Bean
	public CommandLineRunner testMailRunner(JavaMailSender mailSender) {
		return args -> {
			System.out.println(">>> TESTING EMAIL CONFIGURATION ON STARTUP...");
			try {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo("phamquangdung188@gmail.com");
				message.setSubject("Test Email Startup");
				message.setText("Email service is working perfectly!");
				mailSender.send(message);
				System.out.println(">>> EMAIL SENT SUCCESSFULLY TO phamquangdung188@gmail.com!");
			} catch (Exception e) {
				System.err.println(">>> EMAIL SENDING FAILED: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}
