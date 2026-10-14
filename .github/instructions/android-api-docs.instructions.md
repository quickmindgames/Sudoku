---
applyTo: "app/src/main/**/api/**,app/src/main/**/network/**,app/src/main/**/data/**,docs/api-contracts.md,docs/data-flow.md"
---

# API and data documentation

When adding or changing an API service, endpoint, DTO, mapper, repository, cache, authentication flow, or error handling:

1. Update docs/api-contracts.md in the same pull request.
2. Document endpoint purpose, method, path, auth requirement, request/response shape, errors, and consuming repository/service.
3. Update docs/data-flow.md if caching, retry, offline, or synchronization behavior changes.
4. Do not invent request/response fields or behavior.
5. Mark unavailable details as `Not determinable from provided context.`
