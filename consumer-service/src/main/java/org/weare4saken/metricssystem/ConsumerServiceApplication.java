package org.weare4saken.metricssystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ConsumerServiceApplication {
    private static ApplicationContext context;

    public static void main(String[] args) {
        ConsumerServiceApplication.context = SpringApplication.run(ConsumerServiceApplication.class, args);
    }

    public static ApplicationContext getApplicationContext() {
        return ConsumerServiceApplication.context;
    }
}