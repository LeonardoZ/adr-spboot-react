.DEFAULT_GOAL := help

ENV_FILE ?= .env
SERVICE ?=

COMPOSE := docker compose --env-file $(ENV_FILE)

.PHONY: help init config up down restart ps logs recreate-keycloak format-backend test-backend test-frontend test build-backend build-frontend build e2e verify reset

help:
	@printf '%s\n' \
	  'ADR Manager — comandos locais' \
	  '' \
	  'Configuração:' \
	  '  make init                  Cria $(ENV_FILE) a partir de .env.example se necessário.' \
	  '' \
	  'Stack Docker:' \
	  '  make config                Valida a configuração do Docker Compose.' \
	  '  make up                    Cria, reconstrói e aguarda a stack ficar saudável.' \
	  '  make down                  Para e remove os contêineres da stack.' \
	  '  make restart               Reinicia a stack.' \
	  '  make ps                    Exibe o estado dos serviços.' \
	  '  make logs [SERVICE=nome]   Acompanha logs de toda a stack ou de um serviço.' \
	  '  make recreate-keycloak     Recria o Keycloak após alterar a importação do realm.' \
	  '' \
	  'Qualidade e build:' \
	  '  make format-backend         Aplica o Spring Java Format ao backend.' \
	  '  make test-backend          Executa os testes Maven do backend.' \
	  '  make test-frontend         Instala dependências e executa os testes Vitest.' \
	  '  make test                  Executa todos os testes.' \
	  '  make build-backend         Gera o artefato Maven sem executar testes.' \
	  '  make build-frontend        Instala dependências e gera o build Vite.' \
	  '  make build                 Gera os artefatos de backend e frontend.' \
	  '' \
	  'Integração e dados:' \
	  '  make e2e                   Atualiza a stack, aguarda saúde e executa o cenário E2E.' \
	  '  make verify                Executa config, testes, builds e E2E.' \
	  '  make reset                 Remove a stack e os volumes locais de MariaDB e Elasticsearch.' \
	  '' \
	  'Exemplos:' \
	  '  make up' \
	  '  make logs SERVICE=api' \
	  '  make ENV_FILE=.env.local config'

init:
	@if [ -f "$(ENV_FILE)" ]; then \
		printf 'Using existing environment file: %s\n' "$(ENV_FILE)"; \
	else \
		cp .env.example "$(ENV_FILE)"; \
		printf 'Created environment file: %s\n' "$(ENV_FILE)"; \
	fi

config: init
	@$(COMPOSE) config --quiet

up: init
	@$(COMPOSE) up --build --wait

down: init
	@$(COMPOSE) down

restart: down up

ps: init
	@$(COMPOSE) ps

logs: init
	@$(COMPOSE) logs -f $(SERVICE)

recreate-keycloak: init
	@$(COMPOSE) up -d --force-recreate keycloak

format-backend:
	@cd backend && mvn spring-javaformat:apply

test-backend:
	@cd backend && mvn -Dnet.bytebuddy.experimental=true test

test-frontend:
	@cd frontend && npm ci && npm run test

test: test-backend test-frontend

build-backend:
	@cd backend && mvn -DskipTests package

build-frontend:
	@cd frontend && npm ci && npm run build

build: build-backend build-frontend

e2e: up
	@./scripts/compose-e2e.sh

verify: config test build e2e

reset: init
	@$(COMPOSE) down -v
