# Design

## Context

The current Compose stack contains the `spa` Nginx container and the `api` Spring Boot container, both of which emit operational logs to standard output. Developers can currently inspect them only through individual `docker compose logs` commands. See `proposal.md` for motivation and `specs/local-log-observability/spec.md` for the required behavior.

## Goals / Non-Goals

**Goals:**

- Add a self-contained, development-only Elasticsearch, Logstash, and Kibana pipeline to the existing Compose stack.
- Forward only `spa` and `api` standard-output logs, retaining the service identity and timestamp.
- Provide a persistent local Elasticsearch volume and a configurable Kibana host port.
- Make the log workflow discoverable in the README.

**Non-Goals:**

- Capturing browser console output, JavaScript exceptions, traces, metrics, or logs from MariaDB and Keycloak.
- Providing production-grade authentication, authorization, encryption, retention policies, alerting, or centralized deployment.
- Changing frontend or backend application code or APIs.

## Decisions

### Use Filebeat to collect Docker container logs

Run Filebeat with read-only access to the Docker socket and container log directory. Opt in only the `spa` and `api` services through Compose labels; Filebeat's Docker metadata preserves their Compose service identity and forwards events to Logstash through the Compose network.

This retains Docker's default readable logging behavior, so `docker compose logs -f spa api` remains a viable fallback. The alternative—using Docker's GELF logging driver—would have been simpler but makes the Compose logs command unavailable. Filebeat adds one local container and limited read-only Docker access in exchange for preserving that familiar workflow.

### Run a local, compatible ELK trio

Add Elasticsearch in single-node mode, Logstash with a committed Beats input pipeline, Kibana, and Filebeat at matching pinned image versions. Logstash writes events to a dedicated date-partitioned application-log index; Kibana uses that index pattern as its data view.

Elasticsearch security is disabled because this is an explicitly local development service. Elasticsearch, Logstash, and Kibana host ports are configurable through `.env`; Elasticsearch and Logstash bind to loopback only, while Kibana is the browser-facing interface. Kibana receives a development-only encrypted-saved-objects key so it can persist the data view required for log discovery.

### Persist indexed logs and document the browser workflow

Use a named volume for Elasticsearch data so logs survive ordinary `docker compose down` / `up` cycles. The README will cover startup, the Kibana URL, selecting the log data view, filtering by source tag and time, the local-only security boundary, and `docker compose logs -f spa api` as a simple fallback.

## Risks / Trade-offs

- [ELK services consume appreciable local memory and slow stack startup] → Use single-node development settings, small JVM heap limits where supported, and Compose health/dependency checks appropriate to service readiness.
- [Filebeat requires read-only access to Docker metadata and JSON logs] → Mount only the Docker socket and container log directory read-only, and opt in only the two application services with labels.
- [Filebeat-to-Logstash interruption can delay or lose development logs] → Keep direct `docker compose logs` available as a fallback; durability beyond local development is out of scope.
- [Unauthenticated Kibana is unsafe beyond local use] → Bind only the configured local development port and state the development-only limitation in the README.

## Migration Plan

1. Add the ELK and Filebeat services, configurations, volume, environment defaults, and application collection labels to Compose.
2. Start the stack with the documented Compose command and verify health plus log ingestion from both services.
3. Verify the Kibana data view and source-service filtering using generated SPA and API traffic.
4. If the stack must be rolled back, remove the new Compose services and logging configuration; the application containers revert to Docker's default logging behavior. The Elasticsearch volume can be retained or explicitly removed with the existing volume-cleanup workflow.
