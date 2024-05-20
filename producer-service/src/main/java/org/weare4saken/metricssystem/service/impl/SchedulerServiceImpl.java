package org.weare4saken.metricssystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.weare4saken.metricssystem.publisher.MetricsPublisher;
import org.weare4saken.metricssystem.service.SchedulerService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final MetricsPublisher metricsPublisher;

    @Override
    @Scheduled(fixedRate = 30L, initialDelay = 3L, timeUnit = TimeUnit.SECONDS)
    public void schedule() {
        this.metricsPublisher.publish();
    }
}
