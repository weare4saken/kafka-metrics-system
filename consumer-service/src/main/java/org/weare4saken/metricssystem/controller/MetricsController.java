package org.weare4saken.metricssystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.service.Impl.MetricsServiceImpl;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetricsController {

    private final MetricsServiceImpl service;

    @GetMapping
    public ResponseEntity<?> getAllMetrics() {
        List<Metrics> metrics = this.service.findAll();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(metrics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMetrics(@PathVariable String id) {
        Metrics data = this.service.findById(id);

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metrics with id " + id + " not found");
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
    }
}
