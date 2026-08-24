# ADR-001 — Modular Monolith

## Status

Accepted

## Decision

Use a modular monolith for the MVP.

## Reason

The project is early-stage and has no demonstrated scale requiring microservices.

A modular monolith reduces:

- operational complexity
- deployment complexity
- debugging cost
- infrastructure cost

## Consequence

Modules must have clear boundaries so that future extraction remains possible.
