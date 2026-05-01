package com.jobengine.worker.service;

import com.jobengine.common.dto.JobEvent;
import com.jobengine.common.dto.JobStatus;
import com.jobengine.worker.entity.Job;
import com.jobengine.worker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProcessorService {

    private static final double FAILURE_RATE = 0.10; // 10% of jobs will fail

    private final JobRepository jobRepository;

    @Transactional
    public void process(JobEvent event) {
        log.info("Processing job: jobId={}, type={}", event.getJobId(), event.getType());

        // Upsert: find the existing record or create one if the worker receives the event before the producer persists it
        Job job = jobRepository.findById(event.getJobId())
                .orElseGet(() -> Job.builder()
                        .id(event.getJobId())
                        .type(event.getType())
                        .payload(event.getPayload())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        job.setStatus(JobStatus.PROCESSING);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        simulateWork();

        // Throwing here causes DefaultErrorHandler to route the message to job-events.DLT
        if (ThreadLocalRandom.current().nextDouble() < FAILURE_RATE) {
            job.setStatus(JobStatus.FAILED);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
            throw new RuntimeException("Simulated failure for jobId=" + event.getJobId());
        }

        job.setStatus(JobStatus.DONE);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        log.info("Job processed successfully: jobId={}", event.getJobId());
    }

    private void simulateWork() {
        try {
            // ThreadLocalRandom is preferred over Random in multi-threaded environments — no contention
            long delay = ThreadLocalRandom.current().nextLong(100, 501);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status — never swallow this
        }
    }
}
