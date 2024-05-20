package org.weare4saken.metricssystem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.repository.MetricsRepository;
import org.weare4saken.metricssystem.service.Impl.MetricsServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest {

    @Mock
    MetricsRepository repository;

    @InjectMocks
    MetricsServiceImpl service;

    @Test
    void saveMetrics_shouldSaveMetrics() {
        Metrics metricsData = Metrics.builder().name("app.name").data("UP").build();
        ArgumentCaptor<Metrics> captor = ArgumentCaptor.forClass(Metrics.class);
        Mockito.doReturn(metricsData).when(this.repository).save(captor.capture());

        this.service.save(metricsData);

        Assertions.assertEquals(metricsData, captor.getValue());
    }

    @Test
    void saveAllMetrics_shouldSaveAllMetrics() {
        List<Metrics> metricsList = List.of(
                Metrics.builder().name("app.name").data("UP").build(),
                Metrics.builder().name("jvm.info").data(System.getProperty("java.vm.name")).build()
        );
        ArgumentCaptor<List<Metrics>> captor = ArgumentCaptor.captor();
        Mockito.doReturn(metricsList).when(this.repository).saveAll(captor.capture());

        this.service.saveAll(metricsList);

        Assertions.assertEquals(metricsList, captor.getValue());
    }

    @Test
    void findMetrics_shouldFindMetrics() {
        Metrics metricsData = Metrics.builder().name("app.name").data("UP").build();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.doReturn(Optional.of(metricsData)).when(this.repository).findById(captor.capture());

        Metrics result = this.service.findById(metricsData.getName());

        Assertions.assertEquals(metricsData.getName(), captor.getValue());
        Assertions.assertEquals(metricsData, result);
    }

    @Test
    void findAllMetrics_shouldFindAllMetrics() {
        List<Metrics> metricsList = List.of(
                Metrics.builder().name("app.name").data("UP").build(),
                Metrics.builder().name("jvm.info").data(System.getProperty("java.vm.name")).build()
        );
        Mockito.when(this.repository.findAll()).thenReturn(metricsList);

        List<Metrics> resultList = this.service.findAll();

        Mockito.verify(this.repository).findAll();
        Mockito.verifyNoMoreInteractions(this.repository);
        Assertions.assertEquals(metricsList, resultList);
    }
}
