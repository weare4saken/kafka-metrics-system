package org.weare4saken.metricssystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.weare4saken.metricssystem.databind.JsonTreeToStringDeserializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {

    @JsonProperty(value = "name", required = true)
    @NotEmpty
    private String name;

    @JsonProperty(value = "data", required = true)
    @JsonDeserialize(using = JsonTreeToStringDeserializer.class)
    @NotBlank
    private String data;

    @JsonProperty(value = "description")
    private String description = "";
}
