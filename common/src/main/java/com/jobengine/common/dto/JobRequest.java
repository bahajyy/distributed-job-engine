package com.jobengine.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data           // generates getters, setters, equals, hashCode, toString
@Builder        // enables JobRequest.builder().type("EMAIL").build()
@NoArgsConstructor  // required by Jackson for deserialization
@AllArgsConstructor // required by @Builder when @NoArgsConstructor is present
public class JobRequest {

    // Job type determines how the worker will process this job (e.g. EMAIL, REPORT, NOTIFICATION)
    private String type;

    // Flexible payload — structure varies per job type
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();
}
