package org.weare4saken.metricssystem.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.weare4saken.metricssystem.listener.consumer.MetricsMessagesConsumer;
import org.weare4saken.metricssystem.model.Metrics;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsListener {
    private final MetricsMessagesConsumer metricsMessagesConsumer;

    @KafkaListener(
            topics = "${consumer-service.kafka.topic}",
            containerFactory = "listenerContainerFactory")
    public void listen(@Payload List<Message<Metrics>> values) {
        this.metricsMessagesConsumer.accept(values);
    }
}
