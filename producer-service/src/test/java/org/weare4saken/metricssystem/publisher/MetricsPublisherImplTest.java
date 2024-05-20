package org.weare4saken.metricssystem.publisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.publisher.impl.MetricsPublisherImpl;
import org.weare4saken.metricssystem.sender.MetricsSender;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MetricsPublisherImplTest {

    @Mock
    MetricsSender<String, Metrics> metricsSender;

    @Spy
    @InjectMocks
    MetricsPublisherImpl publisher;

    @Test
    void handlePublish_whenMetricsGetsCollected_thenMetricsGetsPublished() throws Exception {
        Metrics appHealth = Metrics.builder().name("app.health").data(Status.UP.toString()).build();
        Metrics jvmInfo = Metrics.builder().name("jvm.info").data(System.getProperty("java.vm.name")).build();
        Mockito.doReturn(List.of(appHealth, jvmInfo)).when(this.publisher).collectMetrics();

        this.publisher.publish();

        Mockito.verify(this.metricsSender).send(appHealth);
        Mockito.verify(this.metricsSender).send(jvmInfo);
    }

    @Test
    void handlePublish_whenCollectingMetricsThrowsException_thenMetricsIsNotPublished() throws Exception {
        Mockito.doThrow(new RuntimeException("Something went wrong")).when(this.publisher).collectMetrics();

        Assertions.assertThrows(Exception.class, () -> this.publisher.publish());

        Mockito.verifyNoInteractions(this.metricsSender);
    }
}
