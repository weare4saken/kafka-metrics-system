package org.weare4saken.metricssystem.listener;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.weare4saken.metricssystem.listener.consumer.MetricsMessagesConsumer;
import org.weare4saken.metricssystem.model.Metrics;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MetricsListenerTest {

    @Mock
    MetricsMessagesConsumer consumer;

    @InjectMocks
    MetricsListener listener;

    @Test
    void handleListen_shouldCallMessagesConsumer() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar");
        MessageHeaders headers = accessor.getMessageHeaders();
        Message<Metrics> message1 = MessageBuilder.createMessage(Metrics.builder().build(), headers);
        Message<Metrics> message2 = MessageBuilder.createMessage(Metrics.builder().build(), headers);
        List<Message<Metrics>> messages = List.of(message1, message2);
        ArgumentCaptor<List<Message<Metrics>>> captor = ArgumentCaptor.captor();
        Mockito.doNothing().when(this.consumer).accept(captor.capture());

        this.listener.listen(messages);

        Assertions.assertEquals(messages, captor.getValue());
    }
}
