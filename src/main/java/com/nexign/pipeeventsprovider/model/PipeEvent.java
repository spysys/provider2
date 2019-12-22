package com.nexign.pipeeventsprovider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class PipeEvent {

    private UUID id;

    @JsonProperty("photo_byte_array")
    private String photoByteArray;

    private String timestamp;

    @JsonProperty("task_id")
    private UUID taskId;

    @JsonProperty("custom_parameters")
    private Map<String, String> customParameters;
}
