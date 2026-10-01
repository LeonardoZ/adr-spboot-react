# Proposal

## Why

Local development currently requires inspecting each container separately, which makes diagnosing frontend proxy traffic and backend API failures unnecessarily difficult. A small, self-contained log stack will provide one searchable view of the application containers' operational logs.

## What Changes

- Add a development-only Elasticsearch, Logstash, Kibana, and Filebeat stack to Compose.
- Use Filebeat to collect the SPA (Nginx) and API (Spring Boot) containers' standard-output logs and forward them to Logstash while preserving the emitting service identity.
- Store the collected logs in Elasticsearch for searching through Kibana.
- Expose Elasticsearch, Logstash, and Kibana on configurable local ports and retain Elasticsearch data in a named volume.
- Document how to start the stack, open Kibana, select the log data view, and filter frontend versus backend logs.
- Keep the scope to container stdout logs; browser-side JavaScript telemetry is excluded.

## Capabilities

### New Capabilities
- `local-log-observability`: Collect and search frontend and backend container logs in the local Compose environment.

### Modified Capabilities

None.

## Impact

- Affects `compose.yml`, `.env.example`, and `README.md`.
- Adds Logstash and Filebeat configuration plus named Elasticsearch storage.
- Adds Elasticsearch, Logstash, Kibana, and Filebeat container images to the local development stack.
- Does not change application APIs, business behavior, or production deployment configuration.
