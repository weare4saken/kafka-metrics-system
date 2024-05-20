package org.weare4saken;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@AllArgsConstructor
public class ProducerServiceApplication {

    private static ApplicationContext context;

    public static void main(String[] args) {
        ProducerServiceApplication.context = SpringApplication.run(ProducerServiceApplication.class, args);
    }

    public static ApplicationContext getApplicationContext() {
        return ProducerServiceApplication.context;
    }
}