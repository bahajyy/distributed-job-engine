# distributed-job-engine — Roadmap

## Phase 1 — Core Services & Kafka

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
- [x] `config/KafkaProducerConfig.java` — KafkaTemplate, topic definitions
- [x] `service/JobProducerService.java` — saves job to DB, publishes to Kafka
- [x] `controller/JobController.java` — POST /jobs endpoint
- [x] `resources/application.yml` — DB, Kafka, actuator config

### Module: worker-service
- [x] `pom.xml`
- [x] `WorkerApplication.java`
- [x] `entity/Job.java`
- [x] `repository/JobRepository.java`
- [x] `config/KafkaConsumerConfig.java` — consumer factory, Dead Letter Topic error handler
- [x] `service/JobProcessorService.java` — 10% fail simulation, 100–500ms delay
- [x] `consumer/JobConsumer.java` — @KafkaListener + DLT handler
- [x] `resources/application.yml`

### Infrastructure
- [x] `docker-compose.yml` — Zookeeper, Kafka, PostgreSQL, Kafka UI, producer-api, worker-service
- [x] `producer-api/Dockerfile`
- [x] `worker-service/Dockerfile`

---

## Phase 2 — Kubernetes

### Step 1 — Dockerfile Improvements
- [ ] `.dockerignore` files for producer-api and worker-service
- [ ] Optimize build layer caching

### Step 2 — GitLab CI/CD Pipeline
- [ ] `.gitlab-ci.yml` — build, test, Docker image build and push to GitLab Container Registry

### Step 3 — Kubernetes Manifests
- [ ] `k8s/namespace.yaml`
- [ ] `k8s/postgres/` — Deployment, Service, PersistentVolumeClaim
- [ ] `k8s/kafka/` — Deployment, Service
- [ ] `k8s/producer-api/` — Deployment, Service, ConfigMap
- [ ] `k8s/worker-service/` — Deployment, Service, ConfigMap

### Step 3b — Networking
- [ ] Ingress Controller (ingress-nginx) — routes external HTTP traffic into the cluster
- [ ] Ingress manifest for producer-api — exposes `POST /jobs` externally
- [ ] NetworkPolicy for worker-service — allow only Kafka and PostgreSQL traffic, deny everything else
- [ ] NetworkPolicy for producer-api — allow only inbound HTTP and outbound Kafka/PostgreSQL

### Step 4 — Secret Management
- [ ] K8s Secrets for DB credentials and sensitive config (base64 encoded, not in ConfigMap)

### Step 5 — Health Probes
- [ ] Liveness probe on `/actuator/health` — K8s restarts the pod if it fails
- [ ] Readiness probe on `/actuator/health` — K8s stops routing traffic if not ready

### Step 6 — Horizontal Pod Autoscaler
- [ ] HPA for worker-service — scale up when CPU > 60%, scale down when idle

### Step 7 — Helm Chart
- [ ] `helm/job-engine/` — templates + values.yaml, full stack deployable with one command
- [ ] Environment overrides: dev / staging / prod via values files

### Step 8 — Local Cluster
- [ ] kind or minikube setup for local Kubernetes testing

---

## Phase 3 — Terraform / IaC
- [ ] S3 backend + DynamoDB state locking
- [ ] EKS cluster or local kind provisioning
- [ ] VPC, subnets, IAM roles (IRSA)
- [ ] Modular Terraform structure

---

## Phase 4 — Observability & SRE
- [ ] Custom Prometheus metrics (job success rate, queue depth, p95 latency)
- [ ] Grafana dashboard — 4 Golden Signals (latency, traffic, errors, saturation)
- [ ] OpenTelemetry tracing — end-to-end: Producer → Kafka → Worker
- [ ] Jaeger integration
- [ ] Alert rules + Runbook (SLO: 99.5% success rate, p95 < 2s)
- [ ] Chaos engineering: Kafka pod kill, network latency injection
