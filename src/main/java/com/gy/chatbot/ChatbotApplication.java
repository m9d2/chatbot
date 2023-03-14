package com.gy.chatbot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Runtime.getRuntime().exec("cmd   /c   start   http://localhost:8080");
    }
}
