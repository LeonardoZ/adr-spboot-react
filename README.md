# ADR Manager

ADR Manager is a local development platform for Architecture Decision Logs
(ADLs) and Architecture Decision Records (ADRs). It consists of a Spring Boot
API, React SPA, MariaDB, and a development-only Keycloak realm.

## Prerequisites

- Java 21 and Maven 3.9+
- Node.js 22+
- Docker Engine with Docker Compose v2

## Local configuration and startup

Create a local environment file from the committed template, then build and
start the complete development stack:

```bash
make init
make up
```

The services are available at:

| Service | Address |
| --- | --- |
| SPA | http://localhost:3000 |
| API | http://localhost:8080 |
| API health | http://localhost:8080/actuator/health |
| API documentation (Swagger UI) | http://localhost:8080/swagger-ui/index.html |
| Keycloak | http://localhost:8081 |
| MariaDB | localhost:3306 |
| Elasticsearch (local only) | http://localhost:9200 |
| Logstash Beats input (local only) | localhost:5044 |
| Kibana | http://localhost:5601 |

Use `make ps` to inspect service health and `make logs SERVICE=<service>` to
follow a service. Stop the stack with `make down`.
The MariaDB data volume is retained; remove it only when a fresh local database
is intended:

```bash
make reset
```

## Make commands

The Makefile provides a convenient workflow for the common local tasks. It
uses `.env` by default; use `ENV_FILE=.env.local` to select a different
environment file.

```bash
make init                     # create .env from .env.example when needed
make config                   # validate Compose configuration
make up                       # build, start, and wait for the stack
make ps                       # inspect service state
make logs SERVICE=api         # follow API logs
make recreate-keycloak        # recreate Keycloak after a realm import change
make down                     # stop and remove the stack
make reset                    # remove the stack and local database/search data

make format-backend           # apply Spring Java Format
make test                     # run backend and frontend tests
make build                    # build the backend and SPA
make e2e                      # run the authenticated E2E scenario
make verify                   # validate config, test, build, and run E2E
```

Backend Java sources follow Spring Java Format. Apply the formatter before
committing backend changes with:

```bash
make format-backend
```

Maven validates formatting automatically during its `validate` phase, including
when `make test-backend` runs.

For example, run the stack with a local environment file using:

```bash
ENV_FILE=.env.local make up
```

`make verify` runs Compose configuration validation, all tests, both builds,
and the authenticated end-to-end scenario.

## Local application logs

The Compose stack includes a development-only Elasticsearch, Logstash, Kibana,
and Filebeat pipeline. Filebeat collects only the `spa` (Nginx) and `api`
(Spring Boot) container logs; it does not collect browser console output,
MariaDB, or Keycloak logs. Elasticsearch and Logstash bind to localhost only,
and the stack has no authentication or production-grade retention policy.

If Docker uses a custom data directory, set `DOCKER_ROOT_DIR` to Docker's root
directory before starting the stack. The default is `/var/lib/docker`.

After starting the stack and generating application traffic, open
http://localhost:5601, then use **Discover** to create a data view named
`adr-manager-logs-*` with `@timestamp` as its time field. Filter the result
with KQL to isolate a service:

```text
container.labels.com_docker_compose_service : "api"
container.labels.com_docker_compose_service : "spa"
```

The first line shows backend API logs; the second shows frontend Nginx access
and error logs. If Kibana has not indexed a new event yet, widen Discover's
time range or generate a request to the SPA or API. Docker's normal log
workflow remains available as a fallback:

```bash
make logs SERVICE='spa api'
```

If a default host port is occupied, override just that port for the command,
for example:

```bash
MARIADB_PORT=3307 API_PORT=18080 SPA_PORT=13000 make up
```

## Environment variables

`.env.example` contains development-only defaults. Run `make init` to create
`.env`, then replace the passwords before sharing an environment. Do not commit
`.env` or use these credentials outside local development.

The current ADL API has no project classification. ADL creation and updates do
not accept a project identifier or responsible-user value. ADLs use
`archivedAt` rather than a status; an archived ADL is read-only and cannot
receive ADR activity. ADL detail responses include an `adrs` array of summaries
(`identifier`, `title`, `status`, `authorDisplayName`, and `createdAt`). ADR
status is limited to `DRAFT`, `UNDER_REVIEW`, `APPROVED`, and `REJECTED`; ADR
replacement/supersession is not supported. The existing nested ADR list and
full ADR detail endpoints remain available. These are breaking API and database
migrations: Flyway migration V3 discards project assignments, project records,
and project audit events, while V4 removes obsolete ADL and ADR fields.

- `MARIADB_DATABASE`, `MARIADB_USER`, `MARIADB_PASSWORD`, and
  `MARIADB_ROOT_PASSWORD` configure MariaDB.
- `MARIADB_PORT`, `KEYCLOAK_PORT`, `API_PORT`, and `SPA_PORT` expose services
  on the host.
- `ELASTICSEARCH_PORT`, `LOGSTASH_PORT`, and `KIBANA_PORT` set the local ELK
  stack ports. Elasticsearch and Logstash bind to `127.0.0.1`; Kibana is
  available through its configured host port.
- `KIBANA_ENCRYPTED_SAVED_OBJECTS_KEY` is a development-only 32+ character key
  that lets Kibana persist its data-view configuration.
- `KEYCLOAK_ADMIN`, `KEYCLOAK_ADMIN_PASSWORD`, and `KEYCLOAK_REALM` configure
  the development identity provider.

The API container derives `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and
`KEYCLOAK_ISSUER_URI` from these Compose settings. Production deployments must
supply their own secrets and issuer URL.

## Development Keycloak realm

Compose imports `keycloak/realm-adr-manager.json` into the `adr-manager` realm.
It includes the sole application role, `USER`, a PKCE SPA client, and a
service-account API client restricted to user lookup. This is a breaking
migration: assign `USER` to every existing application account before upgrading.

| User | Password | Role |
| --- | --- | --- |
| `alice.architect` | `alice-password` | USER |
| `bob.approver` | `bob-password` | USER |
| `carol.admin` | `carol-password` | USER |
| `dave.user` | `dave-password` | USER |

These identities and the `adr-manager-api` client secret are intentionally
development-only fixtures. Run `make recreate-keycloak` after changing the
realm import.

## Testing and builds

```bash
make test
make build
```

These targets resolve the backend and frontend dependencies as needed. Use
`make up` for the supported local application runtime.

## OpenSpec workflow

OpenSpec is this repository's spec-driven development system. Current
capability specifications live in `openspec/specs/`; active proposed work lives
in `openspec/changes/<change-name>/`; completed, archived changes provide the
project history in `openspec/changes/archive/`.

For a behavior, API, or data-model change, first inspect the relevant current
specifications. Create a named change, author its proposal, design, tasks, and
affected spec artifacts, then implement the tasks. Validate the change
strictly before archiving it; archive updates the current capability specs from
the completed change.

See [AGENTS.md](AGENTS.md) for repository-specific contribution rules,
verification expectations, and the supported Makefile workflow.

## Verification

The baseline verification sequence is:

```bash
make verify
```

The SPA uses the Keycloak Authorization Code flow with PKCE. Visiting any
application route while anonymous redirects to the sign-in screen; after login,
the requested route is restored. The SPA client also renews tokens silently and
shows all application navigation to authenticated users with the `USER` realm
role.
Keep `SPA_PORT=3000` when testing this development realm because its allowed
redirect URI is `http://localhost:3000/*`.

The Keycloak token endpoint is
`/realms/adr-manager/protocol/openid-connect/token`; the supplied development
users can be used with the SPA client for local integration testing.

## Compose end-to-end scenario

With the development stack running, execute the complete authenticated API
scenario for ADL and ADR creation, submission, approval, ADR summaries, and
audit consultation:

```bash
make e2e
```

Set `API_URL` or `KEYCLOAK_URL` when using non-default exposed ports. The
script creates uniquely named test records and leaves them available for
inspection.

## Final verification

Verified locally on 2026-10-01: `make verify` and strict OpenSpec validation
for `build-adl-adr-platform` passed. The backend test target includes the Byte
Buddy compatibility flag required in this Java 25 environment because the
current test dependency officially supports Java 24.
