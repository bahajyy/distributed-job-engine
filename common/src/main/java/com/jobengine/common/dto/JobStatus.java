package com.jobengine.common.dto;

public enum JobStatus {
    QUEUED,      // Job received by the producer, not yet picked up by a worker
    PROCESSING,  // Worker has started processing the job
    DONE,        // Job completed successfully
    FAILED       // Job failed — will be routed to the Dead Letter Topic
}
