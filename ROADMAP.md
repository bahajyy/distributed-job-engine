# distributed-job-engine — Roadmap

## Phase 1 — Core Services & Kafka (Weeks 1–3)

### Module: common
- [x] `pom.xml`
- [x] `JobStatus.java` — enum: QUEUED, PROCESSING, DONE, FAILED
- [x] `JobRequest.java` — HTTP request body DTO
- [x] `JobEvent.java` — Kafka message DTO

### Module: producer-api
- [x] `pom.xml`
- [x] `ProducerApplication.java` — Spring Boot entry point
- [x] `entity/Job.java` — JPA entity mapped to `jobs` table
- [x] `repository/JobRepository.java` — Spring Data JPA CRUD
- [ ] `config/KafkaProducerConfig.java` — KafkaTemplate, topic definitions
- [ ] `service/JobProducerService.java` — saves job to DB, publishes to Kafka
- [ ] `controller/JobController.java` — POST /jobs endpoint
- [ ] `resources/application.yml` — DB, Kafka, actuator config

### Module: worker-service
- [ ] `pom.xml`
- [ ] `WorkerApplication.java`
- [ ] `entity/Job.java`
- [ ] `repository/JobRepository.java`
- [ ] `config/KafkaConsumerConfig.java` — consumer factory, Dead Letter Topic error handler
- [ ] `service/JobProcessorService.java` — 10% fail simulation, 100–500ms delay
- [ ] `consumer/JobConsumer.java` — @KafkaListener + DLT handler
- [ ] `resources/application.yml`

### Infrastructure
- [ ] `docker-compose.yml` — Zookeeper, Kafka, PostgreSQL, Kafka UI, producer-api, worker-service
- [ ] `producer-api/Dockerfile`
- [ ] `worker-service/Dockerfile`

---

## Phase 2 — Kubernetes (Weeks 4–7)
- [ ] Multi-stage Dockerfiles (build + runtime layers)
- [ ] K8s manifests: Deployment, Service, ConfigMap, Secret
- [ ] Liveness & readiness probes
- [ ] Horizontal Pod Autoscaler (worker: scale up at CPU 60%)
- [ ] Helm chart — full stack deployable with one command

---

## Phase 3 — Terraform / IaC (Weeks 8–10)
- [ ] S3 backend + DynamoDB state locking
- [ ] EKS cluster or local kind provisioning
- [ ] VPC, subnets, IAM roles (IRSA)
- [ ] Modular Terraform structure

---

## Phase 4 — Observability & SRE (Weeks 11–14)
- [ ] Custom Prometheus metrics (job success rate, queue depth, p95 latency)
- [ ] Grafana dashboard — 4 Golden Signals (latency, traffic, errors, saturation)
- [ ] OpenTelemetry tracing — end-to-end: Producer → Kafka → Worker
- [ ] Jaeger integration
- [ ] Alert rules + Runbook (SLO: 99.5% success rate, p95 < 2s)
- [ ] Chaos engineering: Kafka pod kill, network latency injection
