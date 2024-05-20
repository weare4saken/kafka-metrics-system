package org.weare4saken.metricssystem.service.Impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.repository.MetricsRepository;
import org.weare4saken.metricssystem.service.MetricsService;

import java.util.List;

@Service
@AllArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final MetricsRepository repository;

    @Transactional
    @Override
    public List<Metrics> saveAll(Iterable<Metrics> data) {
        return this.repository.saveAll(data);
    }

    @Transactional
    @Override
    public Metrics save(Metrics data) {
        return this.repository.save(data);
    }

    @Override
    public List<Metrics> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Metrics findById(String id) {
        return this.repository.findById(id).orElse(null);
    }
}
