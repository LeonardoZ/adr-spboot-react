# Sistema de Gestão de Architecture Decision Logs / Architecture Decision Records

## Objetivo

Criar uma plataforma para registrar, acompanhar, revisar e consultar decisões arquiteturais, preservando o histórico e a rastreabilidade das discussões e decisões tomadas.

---

# Épico 1 — Gestão de Acesso

## US-001 — Autenticar usuário

**Como** usuário da plataforma  
**Quero** autenticar-me utilizando minha conta corporativa  
**Para** acessar as funcionalidades do sistema de forma segura.

### Critérios de aceitação

- O sistema deve utilizar o Keycloak como provedor de identidade.
- Usuário autenticado com a role `USER` deve receber acesso às funcionalidades da plataforma.
- Usuários não autenticados não podem acessar recursos protegidos.
- O sistema deve identificar o usuário autenticado por meio do identificador fornecido pelo Keycloak.
- Ao realizar logout, a sessão da aplicação deve ser encerrada.

### Caminhos não felizes

- Se o token estiver ausente, o acesso deve ser negado.
- Se o token estiver expirado, o usuário deve ser direcionado para nova autenticação.
- Se o token for inválido, o acesso deve ser negado.
- Se o Keycloak estiver indisponível, o sistema deve informar que a autenticação está temporariamente indisponível.
- Se o usuário estiver autenticado, mas não possuir acesso à aplicação, o sistema deve negar o acesso.

---

## US-002 — Autorizar usuário autenticado

**Como** responsável pela plataforma  
**Quero** exigir a role `USER` para acesso à aplicação  
**Para** impedir acessos indevidos às funcionalidades da plataforma.

### Critérios de aceitação

- O sistema deve reconhecer apenas a role `USER` fornecida pelo Keycloak.
- Cada funcionalidade protegida deve exigir autenticação e a role `USER`.
- Usuários autenticados devem visualizar todas as ações; regras de estado e autoria continuam aplicáveis.
- A autorização deve ser validada também no backend.

### Caminhos não felizes

- Se um usuário tentar executar uma ação sem permissão, a operação deve ser rejeitada.
- Esconder um botão no frontend não deve ser considerado mecanismo suficiente de autorização.
- Se o token não possuir a role `USER`, o acesso deve ser negado.
- Alterações de permissão no Keycloak devem passar a valer após atualização ou renovação da sessão.

---

# Épico 2 — Gestão de ADLs

## US-010 — Criar ADL

**Como** usuário autenticado  
**Quero** registrar um Architecture Decision Log  
**Para** documentar um problema ou contexto que exige uma decisão arquitetural.

### Dados mínimos

- título;
- contexto;
- problema;
- responsável;
- tags opcionais.

### Critérios de aceitação

- O título deve ser obrigatório.
- O contexto deve ser obrigatório.
- O problema deve ser obrigatório.
- Deve existir um responsável válido.
- O sistema deve gerar um identificador único.
- O ADL deve ser criado inicialmente com status `OPEN`.
- O sistema deve registrar data/hora e usuário criador.

### Caminhos não felizes

- Se um campo obrigatório não for informado, o ADL não deve ser criado.
- Se o usuário não possuir permissão de criação, a operação deve ser rejeitada.
- Se o responsável informado não existir ou não estiver ativo, a criação deve ser rejeitada.
- Se ocorrer falha durante a persistência, nenhum ADL parcialmente criado deve permanecer.
- O identificador do ADL não pode ser informado manualmente pelo usuário.

---

## US-011 — Listar ADLs

**Como** usuário  
**Quero** listar os ADLs cadastrados  
**Para** localizar discussões e decisões arquiteturais existentes.

### Critérios de aceitação

- Deve exibir:
  - identificador;
  - título;
  - status;
  - responsável;
  - data de criação;
  - última atualização.
- A listagem deve possuir paginação.
- Deve ser possível ordenar por campos suportados.
- ADLs arquivados podem ser ocultados por padrão.

### Caminhos não felizes

- Se nenhum ADL for encontrado, deve ser apresentada uma lista vazia, sem erro.
- Se a página solicitada não possuir registros, deve retornar uma página vazia.
- Parâmetros de ordenação inválidos devem ser rejeitados ou substituídos por uma ordenação padrão.
- Usuários sem permissão de consulta não devem receber os dados.

---

## US-012 — Filtrar ADLs

**Como** usuário  
**Quero** filtrar ADLs  
**Para** encontrar rapidamente registros relevantes.

### Filtros

- identificador;
- texto;
- status;
- responsável;
- tag;
- período;
- arquivados/não arquivados.

### Critérios de aceitação

- Deve ser possível combinar filtros.
- Filtros não informados não devem limitar o resultado.
- A busca textual pode considerar título, contexto e problema.

### Caminhos não felizes

- Datas inválidas devem ser rejeitadas.
- Uma data inicial maior que a final deve ser rejeitada.
- Status inexistentes devem ser rejeitados.
- Identificadores inválidos não devem causar erro interno.
- Um filtro sem resultados deve retornar coleção vazia.

---

## US-013 — Visualizar detalhes de um ADL

**Como** usuário  
**Quero** visualizar os detalhes de um ADL  
**Para** entender o contexto da decisão e seus ADRs relacionados.

### Critérios de aceitação

- Devem ser exibidos os dados completos do ADL.
- Devem ser exibidos os ADRs relacionados.
- Deve ser exibido o status atual.
- Deve ser exibido o responsável.
- Deve ser exibida uma lista de resumos dos ADRs relacionados, com identificador, título, status, autor e data de criação.
- Devem ser exibidas datas de criação e atualização.

### Caminhos não felizes

- Se o ADL não existir, o sistema deve retornar `ADL não encontrado`.
- Se o usuário não possuir acesso ao ADL, os dados não devem ser apresentados.
- Um ADL arquivado deve permanecer consultável para usuários autorizados.

---

## US-014 — Atualizar ADL

**Como** responsável ou usuário autorizado  
**Quero** atualizar um ADL  
**Para** manter seu contexto e informações atualizados.

### Critérios de aceitação

- Deve ser possível alterar:
  - título;
  - contexto;
  - problema;
  - responsável;
  - projeto;
  - tags.
- O identificador não pode ser alterado.
- O sistema deve registrar quem realizou a atualização.
- Deve atualizar a data da última modificação.

### Caminhos não felizes

- ADL inexistente não pode ser atualizado.
- Usuário sem permissão não pode atualizá-lo.
- Campos obrigatórios não podem ser removidos.
- O identificador não pode ser alterado.
- ADL arquivado não deve aceitar alteração de conteúdo.
- Se dois usuários editarem simultaneamente, o sistema deve evitar sobrescrita silenciosa de dados.

> Recomenda-se utilizar optimistic locking/versionamento para tratar alterações concorrentes.

---

## US-015 — Arquivar ADL

**Como** usuário autorizado  
**Quero** arquivar um ADL  
**Para** encerrar sua utilização ativa sem apagar o histórico.

### Critérios de aceitação

- O ADL deve assumir o status `ARCHIVED`.
- O sistema deve registrar quem arquivou.
- O sistema deve registrar quando ocorreu o arquivamento.
- O registro deve continuar disponível para consulta.

### Caminhos não felizes

- ADL inexistente não pode ser arquivado.
- ADL já arquivado não deve gerar nova alteração.
- Usuário sem permissão não pode arquivar.
- Um ADL não pode ser arquivado enquanto possuir ADR com status `UNDER_REVIEW`.

---

# Épico 3 — Gestão de ADRs

## US-020 — Criar ADR para um ADL

**Como** usuário autenticado  
**Quero** criar um ADR relacionado a um ADL  
**Para** registrar uma alternativa de decisão arquitetural.

### Dados sugeridos

- título;
- contexto;
- proposta/decisão;
- justificativa;
- consequências;
- vantagens;
- desvantagens;
- riscos;
- referências.

### Critérios de aceitação

- Todo ADR deve estar vinculado a um ADL.
- O ADR deve receber identificador único.
- O ADR deve iniciar no status `DRAFT`.
- Deve ser registrado autor e data de criação.

### Caminhos não felizes

- Não deve ser possível criar ADR para ADL inexistente.
- Não deve ser possível criar ADR em ADL arquivado.
- Título deve ser obrigatório.
- Usuário sem permissão não pode criar ADR.
- Se o vínculo com o ADL falhar, o ADR não deve permanecer parcialmente criado.

---

## US-021 — Listar ADRs de um ADL

**Como** usuário  
**Quero** visualizar os ADRs de um ADL  
**Para** conhecer as alternativas consideradas.

### Critérios de aceitação

- Deve exibir:
  - identificador;
  - título;
  - status;
  - autor;
  - data de criação.
- Deve permitir identificar o ADR aprovado.
- Deve permitir acesso aos detalhes de cada ADR.

### Caminhos não felizes

- ADL inexistente deve retornar erro de recurso não encontrado.
- ADL sem ADRs deve retornar lista vazia.
- Usuário sem acesso ao ADL não deve conseguir listar seus ADRs.

---

## US-022 — Visualizar ADR

**Como** usuário  
**Quero** consultar os detalhes de um ADR  
**Para** compreender a alternativa arquitetural registrada.

### Critérios de aceitação

- Deve exibir os dados completos.
- Deve exibir autor, status e datas.
- Se decidido, deve exibir aprovador/reprovador e justificativa.

### Caminhos não felizes

- ADR inexistente deve retornar `ADR não encontrado`.
- Usuário sem acesso ao ADL relacionado não deve visualizar o ADR.
- ADR rejeitado ou substituído deve continuar consultável.

---

## US-023 — Atualizar ADR

**Como** usuário autenticado  
**Quero** atualizar um ADR  
**Para** refinar a alternativa antes da decisão final.

### Critérios de aceitação

- ADR `DRAFT` pode ser editado.
- O sistema deve registrar data e usuário da alteração.
- O identificador e ADL pai não podem ser alterados.

### Caminhos não felizes

- ADR inexistente não pode ser atualizado.
- Usuário sem permissão não pode alterar.
- ADR `APPROVED` não pode ser alterado.
- ADR `REJECTED` não pode ser alterado.
- ADR `SUPERSEDED` não pode ser alterado.
- ADR `UNDER_REVIEW` deve ter edição bloqueada ou limitada.
- Campos obrigatórios não podem ser removidos.
- Alterações concorrentes devem ser detectadas.

---

# Épico 4 — Workflow de Decisão

## US-030 — Submeter ADR para análise

**Como** autor  
**Quero** submeter um ADR para análise  
**Para** iniciar formalmente o processo de decisão.

### Critérios de aceitação

- Apenas ADR com status `DRAFT` pode ser submetido.
- O status deve ser alterado para `UNDER_REVIEW`.
- Deve ser registrada a data de submissão.
- Deve ser registrado quem submeteu.

### Pré-condições sugeridas

Antes da submissão, devem estar preenchidos:

- título;
- contexto;
- decisão proposta;
- justificativa;
- consequências.

### Caminhos não felizes

- ADR incompleto não pode ser submetido.
- ADR já submetido não pode ser submetido novamente.
- ADR aprovado/rejeitado não pode ser submetido.
- ADR pertencente a ADL arquivado não pode ser submetido.
- Usuário sem permissão não pode realizar a ação.

---

## US-031 — Aprovar ADR

**Como** usuário autenticado  
**Quero** aprovar um ADR  
**Para** registrar formalmente a decisão arquitetural escolhida.

### Critérios de aceitação

- Somente ADR `UNDER_REVIEW` pode ser aprovado.
- O status deve ser alterado para `APPROVED`.
- O sistema deve registrar:
  - aprovador;
  - data/hora;
  - justificativa/comentário.
- O ADL deve ser atualizado para `DECIDED`.

### Regra de negócio

Um ADL pode possuir somente um ADR ativo com status `APPROVED`.

### Caminhos não felizes

- ADR `DRAFT` não pode ser aprovado diretamente.
- ADR já aprovado não pode ser aprovado novamente.
- ADR rejeitado não pode ser aprovado sem fluxo explícito de reabertura.
- Token sem a role `USER` não pode aprovar.
- Um ADR não pode ser aprovado caso outro ADR daquele ADL já esteja aprovado.
- ADL arquivado não pode receber aprovação.
- Em caso de falha, a atualização do ADR e do ADL deve ser transacional.

---

## US-032 — Reprovar ADR

**Como** usuário autenticado  
**Quero** reprovar um ADR  
**Para** registrar que a alternativa foi analisada e descartada.

### Critérios de aceitação

- Apenas ADR `UNDER_REVIEW` pode ser rejeitado.
- Deve existir uma justificativa obrigatória.
- O sistema deve registrar usuário e data.
- O status deve ser alterado para `REJECTED`.

### Caminhos não felizes

- ADR `DRAFT` não pode ser rejeitado diretamente.
- ADR aprovado não pode ser rejeitado.
- Justificativa vazia deve impedir a operação.
- Usuário sem permissão não pode rejeitar.
- ADR pertencente a ADL arquivado não pode ser alterado.

---

## US-033 — Cancelar submissão de ADR

**Como** autor  
**Quero** retirar um ADR da análise  
**Para** corrigir ou complementar seu conteúdo.

### Fluxo

```text
UNDER_REVIEW → DRAFT
```

### Critérios de aceitação

- Apenas ADR `UNDER_REVIEW` pode retornar para `DRAFT`.
- A operação deve ser registrada no histórico.

### Caminhos não felizes

- ADR aprovado ou rejeitado não pode retornar para `DRAFT`.
- Usuário sem permissão não pode cancelar a submissão.
- Pode existir regra impedindo o autor de cancelar caso uma aprovação já esteja em andamento.

---

## US-034 — Substituir decisão arquitetural

**Como** usuário autenticado  
**Quero** registrar que uma ADR aprovada foi substituída  
**Para** preservar o histórico da decisão original.

### Critérios de aceitação

- Apenas ADR `APPROVED` pode ser substituído.
- A ADR anterior deve assumir status `SUPERSEDED`.
- Deve ser registrada a referência para a ADR substituta.
- A nova ADR deve estar aprovada antes ou durante a substituição.

### Exemplo

```text
ADR-023
Status: SUPERSEDED
Superseded by: ADR-087
```

### Caminhos não felizes

- ADR rejeitado não pode ser substituído.
- ADR em `DRAFT` não pode ser substituído.
- Uma ADR não pode substituir a si própria.
- Não deve ser permitido criar ciclos entre ADRs substitutas.
- Usuário sem permissão não pode executar a substituição.

---

# Épico 5 — Histórico e Auditoria

## US-040 — Registrar histórico de alterações

**Como** responsável pela governança  
**Quero** que alterações relevantes sejam registradas  
**Para** garantir rastreabilidade das decisões arquiteturais.

### Eventos que devem gerar histórico

- criação;
- atualização;
- submissão;
- aprovação;
- reprovação;
- arquivamento;
- alteração de responsável;
- substituição de ADR.

### Informações mínimas

- usuário;
- data/hora;
- ação;
- entidade;
- identificador;
- valor anterior;
- valor novo.

### Caminhos não felizes

- O histórico não deve poder ser alterado pelo usuário comum.
- O histórico não deve ser apagado quando um ADL for arquivado.
- Falha na gravação do histórico não deve permitir uma operação crítica sem auditoria, principalmente aprovação/reprovação.

> Para operações críticas, recomenda-se persistir o histórico na mesma transação da operação de negócio.

---

# Regras de Negócio

## BR-001 — Imutabilidade de decisões

ADRs aprovados, rejeitados ou substituídos não podem ter seu conteúdo alterado.

---

## BR-002 — Uma decisão aprovada por ADL

Um ADL pode possuir apenas uma ADR `APPROVED` ativa.

---

## BR-003 — Preservação histórica

Nenhum ADL ou ADR deve ser fisicamente removido pelo fluxo normal da aplicação.

---

## BR-004 — Estados válidos do ADL

```text
OPEN
  ↓
IN_ANALYSIS
  ↓
DECIDED
  ↓
ARCHIVED
```

Transições alternativas permitidas:

```text
OPEN → ARCHIVED

IN_ANALYSIS → ARCHIVED
```

---

## BR-005 — Estados válidos do ADR

```text
DRAFT
   │
   ▼
UNDER_REVIEW
   │
   ├──────► REJECTED
   │
   ▼
APPROVED
   │
   ▼
SUPERSEDED
```

Também é permitido:

```text
UNDER_REVIEW → DRAFT
```

para correções.

---

## BR-006 — Transições inválidas

O sistema deve impedir transições como:

```text
DRAFT → APPROVED

DRAFT → REJECTED

REJECTED → APPROVED

SUPERSEDED → DRAFT

APPROVED → DRAFT
```

---

# Observação sobre o Modelo de Domínio

Existem duas formas principais de interpretar ADL e ADR:

```text
ADL = discussão
ADR = alternativa
```

ou:

```text
ADL = log/agrupador
ADR = decisão registrada
```

Para este sistema, o modelo proposto segue a primeira abordagem:

```text
ADL
 ├─ ADR A → REJECTED
 ├─ ADR B → APPROVED
 └─ ADR C → REJECTED
```

Como, na definição tradicional, um ADR normalmente representa uma decisão arquitetural já registrada, pode ser útil documentar explicitamente essa convenção de domínio.

Uma alternativa seria denominar os registros ainda não decididos como:

- `Decision Proposal`;
- `Decision Option`;
- `ADR Candidate`.

---

# Escopo sugerido para MVP

O MVP pode contemplar:

1. Autenticação com Keycloak.
2. Autorização pela role `USER`.
3. Criar ADL.
4. Listar e filtrar ADLs.
5. Consultar ADL.
6. Atualizar ADL.
7. Arquivar ADL.
8. Criar ADR.
9. Listar ADRs de um ADL.
10. Consultar ADR.
11. Atualizar ADR em `DRAFT`.
12. Submeter ADR para análise.
13. Aprovar ADR.
14. Reprovar ADR.
15. Cancelar submissão.
16. Histórico básico de alterações.

Funcionalidades como comentários, anexos, notificações, relacionamentos avançados entre ADRs, exportação Markdown/PDF e integração com Git podem ser tratadas em fases posteriores.
