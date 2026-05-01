package com.jobengine.producer.controller;

import com.jobengine.common.dto.JobRequest;
import com.jobengine.common.dto.JobStatus;
import com.jobengine.producer.service.JobProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController // @Controller + @ResponseBody — return values are serialized to JSON automatically
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobProducerService jobProducerService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED) // 202: job received and queued, not yet processed
    public Map<String, Object> submitJob(@RequestBody @Valid JobRequest request) {
        UUID jobId = jobProducerService.submitJob(request);
        return Map.of(
                "jobId", jobId,
                "status", JobStatus.QUEUED
        );
    }
}
