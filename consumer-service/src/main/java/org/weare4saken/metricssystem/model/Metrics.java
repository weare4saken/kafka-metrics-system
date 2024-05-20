package org.weare4saken.metricssystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.weare4saken.metricssystem.databind.StringToJsonTreeSerializer;


@Entity
@Table(name = "metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {

    @Id
    @Column(name = "name", nullable = false)
    @JsonProperty(value = "name", required = true)
    private String name;

    @Column(name = "data", nullable = false, columnDefinition = "text")
    @JsonProperty(value = "data", required = true)
    @JsonSerialize(using = StringToJsonTreeSerializer.class)
    private String data;

    @Column(name = "description")
    @JsonProperty(value = "description")
    private String description;
}
