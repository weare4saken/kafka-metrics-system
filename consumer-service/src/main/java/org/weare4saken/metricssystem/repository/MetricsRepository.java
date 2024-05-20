package org.weare4saken.metricssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weare4saken.metricssystem.model.Metrics;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, String> {
}
