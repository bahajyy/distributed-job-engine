# distributed-job-engine

A distributed job queue system built from scratch for learning production-grade DevOps and SRE practices.

## Purpose

The goal is to build and operate a real distributed system end-to-end — from application code through containerization, CI/CD, Kubernetes deployment, infrastructure as code, and observability. Each phase adds a production layer on top of the previous one.

## Architecture

```
Client
  │
  │ POST /jobs
  ▼
Producer API ──── Kafka (job-events) ──── Worker Service ──── PostgreSQL
                        │
                  (on failure)
                        │
                  Kafka (job-events.DLT)
```

The Producer API receives HTTP requests, persists jobs to PostgreSQL, and publishes events to Kafka.
The Worker Service consumes those events, processes them, and updates job status in the database.
Failed jobs are routed to a Dead Letter Topic.

## Modules

| Module | Description | Port |
|--------|-------------|------|
| common | Shared DTOs: JobRequest, JobEvent, JobStatus | - |
| producer-api | REST API — accepts jobs, publishes to Kafka | 8081 |
| worker-service | Kafka consumer — processes jobs, writes to DB | 8082 |

## Tech Stack

| Area | Technology |
|------|------------|
| Backend | Java 21, Spring Boot 3 |
| Messaging | Apache Kafka |
| Database | PostgreSQL |
| Containers | Docker, Docker Compose |
| CI/CD | Jenkins |
| Orchestration | Kubernetes, Helm |
| Infrastructure | Terraform (AWS EKS) |
| Observability | Prometheus, Grafana, OpenTelemetry, Jaeger |

## Job Lifecycle

```
QUEUED → PROCESSING → DONE
                   → FAILED → Dead Letter Topic
```

## Running Locally

```bash
docker compose up -d

curl -X POST http://localhost:8081/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "EMAIL", "payload": {"to": "test@test.com"}}'
```

Kafka UI: http://localhost:8090
