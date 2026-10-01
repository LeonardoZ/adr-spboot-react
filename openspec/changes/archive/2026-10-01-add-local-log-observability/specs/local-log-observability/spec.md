# Spec Delta

## Purpose

Provide a single local interface for developers to find frontend and backend container logs while running the Compose development stack.

## ADDED Requirements

### Requirement: Collect application container logs
The local Compose environment SHALL collect standard-output logs emitted by the SPA and API containers, attach the emitting service identity, and make the collected events available to the local log-search service.

#### Scenario: SPA log is collected
- **WHEN** the SPA container emits an Nginx access or error log while the Compose stack is running
- **THEN** a searchable event is available that identifies the SPA as its source

#### Scenario: API log is collected
- **WHEN** the API container emits a Spring Boot log while the Compose stack is running
- **THEN** a searchable event is available that identifies the API as its source

### Requirement: Search local application logs
The local Compose environment SHALL provide a browser-accessible log interface that lets a developer search the collected SPA and API log events and filter them by source service and time.

#### Scenario: Filter backend logs
- **WHEN** a developer opens the documented local log interface and filters for the API source
- **THEN** only matching API log events in the selected time range are displayed

### Requirement: Document local log access
The project README SHALL document how to start the local stack, open the log interface, select the application-log data view, and distinguish SPA logs from API logs.

#### Scenario: Developer follows log documentation
- **WHEN** a developer follows the README after starting the Compose stack
- **THEN** they can open the log interface and search or filter application container logs without needing container-internal credentials
