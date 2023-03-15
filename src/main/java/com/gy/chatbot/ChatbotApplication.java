package com.gy.chatbot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@SpringBootApplication
public class ChatbotApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Properties props = System.getProperties();
        String name = props.getProperty("os.name");
        if (name.equals("Mac OS X")) {
            Runtime.getRuntime().exec("open http://localhost:8080");
        } else {
            Runtime.getRuntime().exec("cmd   /c   start   http://localhost:8080");
        }
    }
}
