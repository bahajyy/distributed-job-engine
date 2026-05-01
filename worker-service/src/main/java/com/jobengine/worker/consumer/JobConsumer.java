package com.jobengine.worker.consumer;

import com.jobengine.common.dto.JobEvent;
import com.jobengine.worker.service.JobProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final JobProcessorService jobProcessorService;

    @KafkaListener(
            topics = "job-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory" // uses our custom factory with DLT error handler
    )
    public void consume(@Payload JobEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received job: jobId={}, partition={}, offset={}", event.getJobId(), partition, offset);
        jobProcessorService.process(event);
    }

    // Separate group-id so Kafka treats this as an independent consumer from the main worker group
    @KafkaListener(
            topics = "job-events.DLT",
            groupId = "${spring.kafka.consumer.group-id}-dlt"
    )
    public void consumeDlt(@Payload JobEvent event,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.error("Job failed -> DLT: jobId={}, type={}, partition={}", event.getJobId(), event.getType(), partition);
    }
}
