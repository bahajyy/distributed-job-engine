package com.jobengine.producer.repository;

import com.jobengine.producer.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Spring generates the implementation at runtime — no SQL needed for standard CRUD operations
public interface JobRepository extends JpaRepository<Job, UUID> {
}
