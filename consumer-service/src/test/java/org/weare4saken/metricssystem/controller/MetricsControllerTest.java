package org.weare4saken.metricssystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.weare4saken.metricssystem.model.Metrics;
import org.weare4saken.metricssystem.service.Impl.MetricsServiceImpl;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricsController.class)
public class MetricsControllerTest {

    static final String METRICS_ENDPOINT = "/api/v1/metrics";

    @MockBean
    MetricsServiceImpl service;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void handleAllGetMetrics_whenAllMetricsRequested_thenReturnsAllMetrics() throws Exception {
        Metrics healthMetrics = Metrics.builder()
                .name("app.name")
                .data("UP")
                .build();

        Metrics jvmInfoMetrics = Metrics.builder()
                .name("jvm.info")
                .data(System.getProperty("java.vm.name"))
                .description("Java Virtual Machine Information")
                .build();

        Metrics nestedMetrics = Metrics.builder()
                .name("custom.service")
                .data("{\"status\":\"UP\",\"details\":{\"uptime\":1906}}")
                .description("custom service metrics information")
                .build();

        List<Metrics> metricsList = List.of(healthMetrics, jvmInfoMetrics, nestedMetrics);

        Mockito.when(this.service.findAll()).thenReturn(metricsList);

        this.mockMvc.perform(get(MetricsControllerTest.METRICS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray(),
                        jsonPath("$", Matchers.hasSize(3)),
                        jsonPath("$[0].name").value(healthMetrics.getName()),
                        jsonPath("$[0].data").value(healthMetrics.getData()),
                        jsonPath("$[1].name").value(jvmInfoMetrics.getName()),
                        jsonPath("$[1].data").value(jvmInfoMetrics.getData()),
                        jsonPath("$[2].name").value(nestedMetrics.getName()),
                        jsonPath("$[2].data").hasJsonPath(),
                        jsonPath("$[2].data.status").value("UP"),
                        jsonPath("$[2].data.details.uptime").value(1906)
                );

        Mockito.verify(this.service).findAll();
        Mockito.verifyNoMoreInteractions(this.service);
    }

    @Test
    void handleGetMetricsById_whenMetricsRequestedById_thenReturnsRequestedMetrics() throws Exception {
        Metrics nestedMetrics = Metrics.builder()
                .name("custom.service")
                .data("{\"status\":\"UP\",\"details\":{\"uptime\":1906}}")
                .description("custom service metrics information")
                .build();

        Mockito.when(this.service.findById(nestedMetrics.getName())).thenReturn(nestedMetrics);

        this.mockMvc.perform(get(MetricsControllerTest.METRICS_ENDPOINT + "/{id}", nestedMetrics.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name").value(nestedMetrics.getName()),
                        jsonPath("$.data").hasJsonPath(),
                        jsonPath("$.data.status").value("UP"),
                        jsonPath("$.data.details.uptime").value(1906),
                        jsonPath("$.description").value(nestedMetrics.getDescription())
                );

        Mockito.verify(this.service).findById(nestedMetrics.getName());
        Mockito.verifyNoMoreInteractions(this.service);
    }

    @Test
    void handleGetMetricsById_whenMetricsNotFound_thenReturnsNotFoundError() throws Exception {
        Metrics nestedMetrics = Metrics.builder()
                .name("custom.service")
                .data("{\"status\":\"UP\",\"details\":{\"uptime\":1906}}")
                .description("custom service metrics information")
                .build();

        Mockito.when(this.service.findById(nestedMetrics.getName())).thenReturn(null);

        this.mockMvc.perform(get(MetricsControllerTest.METRICS_ENDPOINT + "/{id}", nestedMetrics.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.title").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                        jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()),
                        jsonPath("$.detail").value(Matchers.containsString(nestedMetrics.getName() + " not found"))
                );

        Mockito.verify(this.service).findById(nestedMetrics.getName());
        Mockito.verifyNoMoreInteractions(this.service);
    }

    @Test
    void handleAnyException_whenExceptionThrown_thenReturnsBadRequestError() throws Exception {
        String errorMessage = "An unexpected error occurred";

        Mockito.when(service.findAll()).thenThrow(new RuntimeException(errorMessage));

        this.mockMvc.perform(get("/api/v1/metrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }
}