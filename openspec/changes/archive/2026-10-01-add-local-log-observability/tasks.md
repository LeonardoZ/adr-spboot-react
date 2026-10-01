# Tasks

## 1. Local ELK pipeline

- [x] 1.1 Add compatible single-node Elasticsearch, Logstash, Kibana, and Filebeat services, the Elasticsearch data volume, readiness dependencies, Kibana encryption key, and configurable ELK ports to the Compose environment; verify `docker compose --env-file .env.example config --quiet` succeeds.
- [x] 1.2 Add committed Filebeat and Logstash configurations to collect opted-in Docker logs through the Beats input and index their timestamps and service metadata; verify both configurations are mounted by the rendered Compose configuration.

## 2. Application log routing and developer guidance

- [x] 2.1 Configure only the `spa` and `api` Compose services to opt in to Filebeat collection with stable service metadata while retaining Docker's default logging driver; verify the rendered services contain the expected labels and no custom logging driver.
- [x] 2.2 Update `.env.example` and the README with the local-only ELK boundary, Kibana URL, data-view and source-filter workflow, and `docker compose logs -f spa api` fallback; verify every documented startup and access command matches the Compose configuration.

## 3. End-to-end verification

- [ ] 3.1 Start the complete stack with `docker compose --env-file .env.example up --build --wait`, generate SPA and API traffic, and verify searchable events arrive in the application-log index with both Compose service identities.
- [ ] 3.2 Verify the documented Kibana workflow displays and filters SPA versus API logs and that `docker compose logs -f spa api` remains available, then stop the stack without deleting the Elasticsearch volume.
