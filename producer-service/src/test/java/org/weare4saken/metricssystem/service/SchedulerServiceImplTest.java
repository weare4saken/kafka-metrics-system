package org.weare4saken.metricssystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weare4saken.metricssystem.publisher.MetricsPublisher;
import org.weare4saken.metricssystem.service.impl.SchedulerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceImplTest {

    @Mock
    MetricsPublisher metricsPublisher;

    @InjectMocks
    SchedulerServiceImpl service;

    @Test
    void shouldCallPublishMethod() {
        this.service.schedule();

        Mockito.verify(this.metricsPublisher).publish();
    }
}
