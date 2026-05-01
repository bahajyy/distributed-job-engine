# distributed-job-engine

A production-grade distributed job queue system built from scratch.

## Architecture

```
[Client] --> POST /jobs --> [Producer API] --> Kafka (job-events) --> [Worker Service] --> PostgreSQL
                                                                              |
                                                                    (on failure)
                                                                              |
                                                                     Kafka (job-events.DLT)
```

The Producer API receives HTTP requests, persists jobs to PostgreSQL, and publishes events to Kafka.
The Worker Service consumes those events, processes the jobs, and updates their status in the database.
Failed jobs are routed to a Dead Letter Topic for observability and reprocessing.

## Tech Stack

| Layer         | Technology                                  |
|---------------|---------------------------------------------|
| Backend       | Java 21, Spring Boot                        |
| Messaging     | Apache Kafka                                |
| Database      | PostgreSQL 16                               |
| Containers    | Docker, Docker Compose                      |
| Orchestration | Kubernetes, Helm                            |
| IaC           | Terraform (AWS EKS)                         |
| Observability | Prometheus, Grafana, OpenTelemetry, Jaeger  |
| CI/CD         | GitLab CI/CD                                |

## Modules

| Module         | Description                                      | Port |
|----------------|--------------------------------------------------|------|
| common         | Shared DTOs: JobRequest, JobEvent, JobStatus     | -    |
| producer-api   | REST API — accepts jobs and publishes to Kafka   | 8081 |
| worker-service | Kafka consumer — processes jobs and writes to DB | 8082 |

## Job Lifecycle

```
QUEUED --> PROCESSING --> DONE
                     \--> FAILED --> Dead Letter Topic
```

## Running Locally

```bash
docker-compose up -d

curl -X POST http://localhost:8081/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "EMAIL", "payload": {"to": "test@test.com"}}'
```

Kafka UI is available at http://localhost:8080.
