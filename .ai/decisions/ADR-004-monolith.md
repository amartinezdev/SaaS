# ADR-004 — No Premature Distributed Infrastructure

## Status

Accepted

## Decision

Do not add queues, Redis, Kafka, Kubernetes or microservices until a concrete requirement exists.

## Reason

The initial product needs speed of iteration and low operational cost.

## Revisit when

- generation throughput becomes a bottleneck;
- API latency requires asynchronous workflows;
- worker scaling becomes necessary;
- operational requirements justify the complexity.
