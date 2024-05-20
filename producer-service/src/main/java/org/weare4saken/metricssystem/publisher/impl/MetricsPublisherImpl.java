package org.weare4saken.metricssystem.publisher.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Component;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.publisher.MetricsPublisher;
import org.weare4saken.metricssystem.sender.MetricsSender;
import org.weare4saken.metricssystem.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsPublisherImpl implements MetricsPublisher {

    private final MetricsSender<String, Metrics> metricsSender;
    private final MetricsEndpoint metricsEndpoint;
    private final HealthEndpoint healthEndpoint;

    @Override
    public void publish() {
        try {
            for (Metrics metrics : this.collectMetrics()) {
                this.metricsSender.send(metrics);
            }
        } catch (Exception e) {
            log.error("Error while publishing metrics data", e);
            throw new RuntimeException(e);
        }
    }

    public List<Metrics> collectMetrics() throws JsonProcessingException {
        return Stream.of(healthMetrics(), commonMetrics())
                .flatMap(List::stream)
                .toList();
    }

    public List<Metrics> healthMetrics() throws JsonProcessingException {
        return Collections.singletonList(Metrics.builder()
                .name("health")
                .data(JsonUtils.stringify(this.healthEndpoint.health()))
                .description("Detailed information about the health of the application")
                .build());
    }

    public List<Metrics> commonMetrics() throws JsonProcessingException {
        List<Metrics> resultList = new ArrayList<>();

        for (var descriptorName : this.metricsEndpoint.listNames().getNames()) {
            MetricsEndpoint.MetricDescriptor metric = this.metricsEndpoint.metric(descriptorName, null);

            Metrics metrics = Metrics.builder()
                    .name(descriptorName)
                    .data(JsonUtils.stringify(metric))
                    .description(Optional.ofNullable(metric.getDescription()).orElse("")).build();

            resultList.add(metrics);
        }

        return resultList;
    }
}
