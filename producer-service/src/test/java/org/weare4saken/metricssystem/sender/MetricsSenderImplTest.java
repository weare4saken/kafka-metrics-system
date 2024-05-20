package org.weare4saken.metricssystem.sender;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.sender.impl.MetricsSenderImpl;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class MetricsSenderImplTest {

    @Mock
    NewTopic topic;

    @Mock
    KafkaTemplate<String, Metrics> kafkaTemplate;

    @InjectMocks
    MetricsSenderImpl sender;

    @Test
    void handleSend_whenSendingMetricsReturnsCompletableFutureNormally_thenMetricsConsideredSuccessfullySent() throws Exception {
        Metrics metricsData = Metrics.builder().name("app.health").data("OK").build();
        SendResult<String, Metrics> sendResult = Mockito.mock(SendResult.class);
        Mockito.when(this.topic.name()).thenReturn("metrics-topic");
        Mockito.when(this.kafkaTemplate.send(this.topic.name(), metricsData.getName(), metricsData))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        CompletableFuture<SendResult<String, Metrics>> result = this.sender.send(metricsData);

        Mockito.verify(this.kafkaTemplate).send(this.topic.name(), metricsData.getName(), metricsData);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isDone());
        Assertions.assertFalse(result.isCompletedExceptionally());
    }

    @Test
    void handleSend_whenSendingMetricsReturnsCompletableFutureExceptionally_thenSendingMetricsConsideredFailed() throws Exception {
        Metrics metricsData = Metrics.builder().name("app.health").data("OK").build();
        Mockito.when(this.topic.name()).thenReturn("metrics-topic");
        Mockito.when(this.kafkaTemplate.send(this.topic.name(), metricsData.getName(), metricsData))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Something went wrong")));

        CompletableFuture<SendResult<String, Metrics>> result = this.sender.send(metricsData);

        Mockito.verify(this.kafkaTemplate).send(this.topic.name(), metricsData.getName(), metricsData);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isCompletedExceptionally());
    }

    @Test
    void handleSend_whenSendingMetricsThrowsException_thenSendingMetricsConsideredFailed() {
        Metrics metricsData = Metrics.builder().name("app.health").data("OK").build();
        Mockito.when(this.topic.name()).thenReturn("metrics-topic");
        Mockito.when(this.kafkaTemplate.send(this.topic.name(), metricsData.getName(), metricsData))
                .thenThrow(new RuntimeException("Something went wrong"));

        Assertions.assertThrows(Exception.class, () -> this.sender.send(metricsData));
        Mockito.verify(this.kafkaTemplate).send(this.topic.name(), metricsData.getName(), metricsData);
    }
}
