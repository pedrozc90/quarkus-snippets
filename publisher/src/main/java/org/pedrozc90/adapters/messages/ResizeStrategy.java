package org.pedrozc90.adapters.messages;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ResizeStrategy {

    FIXED_DIMENSIONS,
    MAINTAIN_ASPECT_RATIO,
    FIT_TO_BOX;

}
