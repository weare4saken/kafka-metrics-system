package org.weare4saken.metricssystem.listener.consumer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.weare4saken.metricssystem.listener.consumer.MetricsMessagesConsumer;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.service.Impl.MetricsServiceImpl;
import org.weare4saken.metricssystem.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsMessagesConsumerImpl implements MetricsMessagesConsumer {

    private final MetricsServiceImpl metricsService;

    @Override
    public void accept(List<Message<Metrics>> metricsData) {
        Map<String, Map<String, Object>> nestedMetrics = new HashMap<>();
        Map<String, String> plainMetrics = new HashMap<>();

        metricsData.forEach(message -> {
            Metrics payload = message.getPayload();
            try {
                String data = payload.getData();
                if (JsonUtils.isObject(data)) {
                    Map<String, Object> map = JsonUtils.readAsMap(data);
                    nestedMetrics.put(payload.getName(), map);
                } else {
                    plainMetrics.put(payload.getName(), payload.getData());
                }

                Metrics save = this.metricsService.save(payload);
                log.info("Metrics '{}' has been persisted", save.getName());
            } catch (JsonProcessingException e) {
                log.error("Error while converting metrics message to json", e);
            }
        });

        this.processMetrics(nestedMetrics, plainMetrics);
    }

    private void processMetrics(Map<String, Map<String, Object>> nestedMetrics,
                                Map<String, String> plainMetrics) {
        CompletableFuture.runAsync(() -> {
            log.info("Processing nested metrics...: {}", nestedMetrics.size());
            log.info("Processing plain metrics...: {}", plainMetrics.size());
        });
    }
}