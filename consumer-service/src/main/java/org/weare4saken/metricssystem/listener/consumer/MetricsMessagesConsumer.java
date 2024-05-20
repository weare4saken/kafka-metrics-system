package org.weare4saken.metricssystem.listener.consumer;

import org.springframework.messaging.Message;
import org.weare4saken.metricssystem.model.Metrics;

import java.util.List;

public interface MetricsMessagesConsumer extends ListenerConsumer<List<Message<Metrics>>> {
}
