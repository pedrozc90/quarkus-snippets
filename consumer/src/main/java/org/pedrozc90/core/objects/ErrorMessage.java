package org.pedrozc90.core.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ErrorMessage implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "status")
    private Integer status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "error")
    private String error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "message")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "stack")
    private String stack;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "violations")
    private List<Violation> violations;

}
