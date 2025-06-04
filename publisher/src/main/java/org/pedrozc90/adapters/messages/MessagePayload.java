package org.pedrozc90.adapters.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MessagePayload {

    @JsonProperty(value = "uuid")
    private UUID uuid;

    @Builder.Default
    @JsonProperty(value = "strategy")
    private ResizeStrategy strategy = ResizeStrategy.MAINTAIN_ASPECT_RATIO;

}
