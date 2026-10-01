# Contributor Guide

## Project map

- `backend/` is the Spring Boot API. Java code is organized by domain under
  `src/main/java/com/example/adrmanager/` (`adl`, `adr`, `audit`, `identity`,
  `security`, and shared `api`/`config` code). Flyway migrations live in
  `backend/src/main/resources/db/migration/`; backend tests mirror production
  packages under `backend/src/test/`.
- `frontend/` is the React and Vite single-page application. Keep UI, API
  clients, and focused tests together in the relevant feature directory under
  `frontend/src/` (for example, `adl/`, `adr/`, `audit/`, and `auth/`).
- `compose.yml` defines the local Compose stack: API, SPA, MariaDB, Keycloak,
  and the development-only Elasticsearch/Logstash/Kibana/Filebeat pipeline.
  Supporting service configuration is in `keycloak/`, `filebeat/`, and
  `logstash/`.
- `openspec/specs/` contains the current capability specifications.
  `openspec/changes/<change-name>/` contains active change artifacts, and
  `openspec/changes/archive/` contains completed history.

## Local workflow

Use the Makefile commands for the supported local workflow:

```bash
make init                    # create .env from .env.example when needed
make config                  # validate Compose configuration
make up                      # build and start the stack, waiting for health
make ps                      # inspect service state
make logs SERVICE=api        # follow all logs, or one service's logs
make down                    # stop and remove stack containers
make restart                 # restart the stack

make test-backend            # run Maven tests
make test-frontend           # install dependencies and run Vitest
make test                    # run all unit tests
make build-backend           # package the API without tests
make build-frontend          # install dependencies and build the SPA
make build                   # build both applications
make e2e                     # start the stack and run the authenticated E2E scenario
make verify                  # config, tests, builds, and E2E
make reset                   # remove the stack and local MariaDB/Elasticsearch volumes
```

`make reset` deletes local database and search volumes. Use it only when a
fresh local state is intended. Use `ENV_FILE=.env.local` with Make commands
when working from another environment file.

## Contribution expectations

- Keep implementation code grouped by its domain. Avoid introducing a broad
  shared layer when the behavior belongs to an existing backend or frontend
  feature.
- Add or update focused tests with every behavior change. Run the focused
  backend or frontend tests while iterating; run `make verify` before handing
  off changes that affect integration, configuration, authentication, schema,
  or both applications.
- Preserve authentication boundaries. The SPA uses Keycloak Authorization Code
  flow with PKCE, and the API validates Keycloak JWTs and uses its restricted
  service client only for user lookup.
- Treat database changes as Flyway migrations. Do not rewrite migrations that
  may already be applied; add a new migration and cover the resulting behavior.
- Do not commit `.env` files, real credentials, local volumes, build outputs,
  or generated dependency directories. Development realm accounts and secrets
  are fixtures only and must not be reused outside local development.

## OpenSpec policy

Use an OpenSpec change for behavior, API, or data-model changes. Keep work in
`openspec/changes/<change-name>/`, and update the affected capability specs as
part of that change. Before completion, validate it strictly:

```bash
openspec validate <change-name> --strict
```

After implementation and validation are complete, archive the change with
OpenSpec. Archiving synchronizes its accepted spec changes into
`openspec/specs/`; do not manually edit archived history.

```bash
openspec archive <change-name>
```

Documentation should describe user-visible behavior and supported operational
commands. Keep README guidance practical and update it when setup, services,
or contributor workflows change.
