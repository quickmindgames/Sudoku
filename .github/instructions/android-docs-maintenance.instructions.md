---
applyTo: "**"
---

# Android documentation maintenance

After completing a non-trivial change, determine whether any existing documentation is affected.

Update only the directly relevant files:
- docs/architecture.md
- docs/module-map.md
- docs/data-flow.md
- docs/api-contracts.md
- docs/domain-model.md
- docs/decisions.md
- docs/onboarding.md
- docs/pitfalls.md
- docs/glossary.md
- docs/conventions.md

Rules:
- Do not recreate documentation files.
- Make minimal, targeted edits.
- Do not rewrite unrelated sections.
- Do not invent APIs, modules, commands, dependencies, permissions, or business rules.
- If no documentation is affected, state: `No documentation updates required.`
- Add new reusable risks to docs/pitfalls.md.
- Add new reusable conventions to docs/conventions.md.
- Add new domain terms, entities, or module names to docs/glossary.md.
- Record significant architectural choices in docs/decisions.md.
