package org.weare4saken.metricssystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("consumer-service.kafka")
public class KafkaCustomProperties {

    private String topic;
}