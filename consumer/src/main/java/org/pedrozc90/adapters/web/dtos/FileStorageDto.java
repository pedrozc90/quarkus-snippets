package org.pedrozc90.adapters.web.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class FileStorageDto implements Serializable {

    @JsonProperty(value = "id")
    private Long id;

    @NotNull
    @JsonProperty(value = "uuid", required = true)
    private UUID uuid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty(value = "inserted_at")
    private Instant insertedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty(value = "updated_at")
    private Instant updatedAt;

    @JsonProperty(value = "version")
    private Long version;

    @NotNull
    @JsonProperty(value = "hash")
    private String hash;

    @NotNull
    @JsonProperty(value = "filename")
    private String filename;

    @NotNull
    @JsonProperty(value = "content_type")
    private String contentType;

    @NotNull
    @JsonProperty(value = "charset")
    private String charset;

    @NotNull
    @JsonProperty(value = "length")
    private Long length;

    @JsonProperty(value = "space")
    public String space;

}
