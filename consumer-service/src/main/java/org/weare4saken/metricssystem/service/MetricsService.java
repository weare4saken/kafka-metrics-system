package org.weare4saken.metricssystem.service;

import org.weare4saken.metricssystem.model.Metrics;

import java.util.List;

public interface MetricsService {

    List<Metrics> saveAll(Iterable<Metrics> data);

    Metrics save(Metrics data);

    List<Metrics> findAll();

    Metrics findById(String id);
}
