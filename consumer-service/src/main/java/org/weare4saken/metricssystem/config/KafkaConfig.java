package org.weare4saken.metricssystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.JacksonUtils;
import org.weare4saken.metricssystem.model.Metrics;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

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
    public ConsumerFactory<String, Metrics> consumerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties(null);

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TYPE_MAPPINGS,
                "org.weare4saken.metricssystem.model.Metrics:org.weare4saken.metricssystem.model.Metrics");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10_000);

        DefaultKafkaConsumerFactory<String, Metrics> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(properties);
        kafkaConsumerFactory.setValueDeserializer(new JsonDeserializer<>(mapper));

        return kafkaConsumerFactory;
    }

    @Bean("listenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Metrics>>
    listenerContainerFactory(ConsumerFactory<String, Metrics> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Metrics>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);

        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("k-consumer-");
        factory.getContainerProperties().setListenerTaskExecutor(simpleAsyncTaskExecutor);

        return factory;
    }
}