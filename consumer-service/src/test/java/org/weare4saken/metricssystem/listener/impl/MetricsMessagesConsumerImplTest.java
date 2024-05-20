package org.weare4saken.metricssystem.listener.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.weare4saken.metricssystem.listener.consumer.impl.MetricsMessagesConsumerImpl;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.service.Impl.MetricsServiceImpl;
import org.weare4saken.metricssystem.utils.JsonUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MetricsMessagesConsumerImplTest {

    @Mock
    MetricsServiceImpl service;

    @InjectMocks
    MetricsMessagesConsumerImpl consumer;

    @Test
    void whenAccepted_shouldCallSaveMetricsOfDatabaseService() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar");
        MessageHeaders headers = accessor.getMessageHeaders();

        Metrics healthMetrics = Metrics.builder()
                .name("app.name")
                .data("UP")
                .build();

        Metrics jvmInfoMetrics = Metrics.builder()
                .name("jvm.info")
                .data(System.getProperty("java.vm.name"))
                .build();

        Metrics nestedMetrics = Metrics.builder()
                .name("custom.service")
                .data("{\"status\":\"UP\",\"details\":{\"uptime\":1906}}")
                .description("custom service metrics information")
                .build();

        List<Metrics> metricsList = List.of(healthMetrics, jvmInfoMetrics, nestedMetrics);

        List<Message<Metrics>> messages = metricsList.stream()
                .map(m -> MessageBuilder.createMessage(m, headers))
                .toList();

        ArgumentCaptor<Metrics> captor = ArgumentCaptor.forClass(Metrics.class);
        Mockito.doReturn(healthMetrics, jvmInfoMetrics, nestedMetrics).when(this.service).save(captor.capture());

        try (MockedStatic<JsonUtils> mockedStatic = Mockito.mockStatic(JsonUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(JsonUtils::getObjectMapper).thenReturn(JacksonUtils.enhancedObjectMapper());
            this.consumer.accept(messages);
        }

        Assertions.assertEquals(metricsList, captor.getAllValues());
    }
}
