package com.gy.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@SpringBootApplication
public class ChatbotApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String url = "http://localhost:" + environment.getProperty("local.server.port");
        Properties props = System.getProperties();
        String name = props.getProperty("os.name");
        if (name.contains("Mac OS")) {
            Runtime.getRuntime().exec("open " + url);
        }
        if (name.contains("Windows")) {
            Runtime.getRuntime().exec("cmd /c start " + url);
        }
    }
}
