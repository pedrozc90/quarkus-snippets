package org.pedrozc90.adapters.web.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class HealthResponse {

    @NotNull
    @JsonProperty(value = "app", required = true)
    private String app;

    @NotNull
    @Builder.Default
    @JsonProperty(value = "online", required = true)
    private boolean online = true;

    @NotNull
    @JsonProperty(value = "mode", required = true)
    private String mode;

    @NotNull
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty(value = "timestamp", required = true)
    private Instant timestamp = Instant.now();

    @NotNull
    @Builder.Default
    @JsonProperty(value = "timezone", required = true)
    private ZoneId timezone = ZoneId.systemDefault();

}
