package org.weare4saken.metricssystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.sender.MetricsSender;

import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricsController.class)
public class MetricsControllerTest {

    static final String METRICS_ENDPOINT = "/api/v1/metrics";

    @MockBean
    MetricsSender<String, Metrics> metricsSender;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void handlePublishMetrics_whenPayloadIsValid_thenReturnsValidResponse() throws Exception {
        Metrics metrics = new Metrics("app.health", Status.UP.toString(), "");
        SendResult<String, Metrics> sendResult = Mockito.mock(SendResult.class);
        Mockito.when(this.metricsSender.send(metrics)).thenReturn(CompletableFuture.completedFuture(sendResult));

        this.mockMvc.perform(post(MetricsControllerTest.METRICS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(metrics)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        Mockito.verify(this.metricsSender).send(metrics);
    }

    @Test
    void handlePublishMetrics_whenPayloadIsInvalid_thenReturnsErrorResponse() throws Exception {
        String invalidData = null;
        Metrics metrics = new Metrics("app.health", invalidData, "");
        SendResult<String, Metrics> sendResult = Mockito.mock(SendResult.class);
        Mockito.when(this.metricsSender.send(metrics)).thenReturn(CompletableFuture.completedFuture(sendResult));

        this.mockMvc.perform(post(MetricsControllerTest.METRICS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(metrics)))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.detail").value(Matchers.containsString("Error: data"))
                );

        Mockito.verifyNoInteractions(this.metricsSender);
    }

    @Test
    void handlePublishMetrics_whenSendingMetricsFails_thenReturnsErrorResponse() throws Exception {
        Metrics metrics = new Metrics("app.health", "UP", "");
        Mockito.when(this.metricsSender.send(metrics))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Failed to send metrics")));

        this.mockMvc.perform(post(MetricsControllerTest.METRICS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(metrics)))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.detail").value(Matchers.containsString("Failed to send metrics"))
                );

        Mockito.verify(this.metricsSender).send(metrics);
    }

    @Disabled
    @Test
    void handlePublishMetrics_whenSendingMetricsTimeoutReached_thenReturnsErrorResponse() throws Exception {
        Metrics metrics = new Metrics("app.health", "UP", "");
        SendResult<String, Metrics> sendResult = Mockito.mock(SendResult.class);
        Mockito.when(this.metricsSender.send(metrics))
                .thenReturn(CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(62000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return sendResult;
                }));

        this.mockMvc.perform(post(MetricsControllerTest.METRICS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(metrics)))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.detail").value(Matchers.containsString("Failed to send metrics"))
                );

        Mockito.verify(this.metricsSender).send(metrics);
    }
}
