# .ai — Project Brain

This directory is the project's structured knowledge base for AI-assisted development.

## Directory roles

| Directory | Purpose |
|---|---|
| `context/` | What the project is and how it is structured |
| `rules/` | Engineering constraints and coding rules |
| `domain/` | Business/domain semantics |
| `ai/` | AI providers, prompts, validation and cost strategy |
| `platforms/` | Platform-specific listing rules |
| `product/` | Business/product strategy |
| `decisions/` | Architecture Decision Records |
| `state/` | Short-lived project state |
| `tasks/` | MVP and backlog |
| `skills/` | Reusable procedures for AI-assisted coding |

## Retrieval principle

Do not read every file for every task.

Use the smallest useful context set.

## Maintenance

When code changes observable behavior, update the relevant documentation.

When architecture changes, create or update an ADR.
