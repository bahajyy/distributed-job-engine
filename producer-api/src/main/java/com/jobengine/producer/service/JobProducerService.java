package com.jobengine.producer.service;

import com.jobengine.common.dto.JobEvent;
import com.jobengine.common.dto.JobRequest;
import com.jobengine.common.dto.JobStatus;
import com.jobengine.producer.entity.Job;
import com.jobengine.producer.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor // injects JobRepository and KafkaTemplate via constructor
public class JobProducerService {

    private static final String TOPIC = "job-events";

    private final JobRepository jobRepository;
    private final KafkaTemplate<String, JobEvent> kafkaTemplate;

    @Transactional // if Kafka send fails and throws, the DB insert is rolled back
    public UUID submitJob(JobRequest request) {
        // Persist the job first — assigns the UUID we'll use as the Kafka message key
        Job job = Job.builder()
                .status(JobStatus.QUEUED)
                .type(request.getType())
                .payload(request.getPayload())
                .build();

        job = jobRepository.save(job);

        // Build the Kafka event from the persisted job
        JobEvent event = JobEvent.builder()
                .jobId(job.getId())
                .type(job.getType())
                .payload(job.getPayload())
                .status(JobStatus.QUEUED)
                .build();

        // jobId as the message key — ensures all events for the same job go to the same partition
        kafkaTemplate.send(TOPIC, job.getId().toString(), event);
        log.info("Job submitted: jobId={}, type={}", job.getId(), job.getType());

        return job.getId();
    }
}
