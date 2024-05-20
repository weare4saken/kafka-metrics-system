package org.weare4saken.metricssystem.sender;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface MetricsSender<T, R> {

    CompletableFuture<SendResult<T, R>> send(R value) throws Exception;
}
