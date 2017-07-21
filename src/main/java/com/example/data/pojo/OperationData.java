package com.example.data.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 */
@JsonPropertyOrder({
        "operations"
})
public class OperationData {

    @JsonProperty("operations")
    private Operation[] operations;

    @JsonProperty("operations")
    public Operation[] getOperations() {
        return operations;
    }

    @JsonProperty("operations")
    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }
}
