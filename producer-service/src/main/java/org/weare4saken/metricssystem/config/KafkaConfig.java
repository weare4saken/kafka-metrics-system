package org.weare4saken.metricssystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.weare4saken.metricssystem.model.Metrics;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    public final KafkaCustomProperties kafkaCustomProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public ProducerFactory<String, Metrics> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties(null);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, Metrics> factory = new DefaultKafkaProducerFactory<>(properties);
        factory.setValueSerializer(new JsonSerializer<>(mapper));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Metrics> kafkaTemplate(ProducerFactory<String, Metrics> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(this.kafkaCustomProperties.getTopic())
                .partitions(1)
                .replicas(1)
                .compact()
                .config(TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG, String.valueOf(TimeUnit.MINUTES.toMillis(5)))
                .build();
    }
}
