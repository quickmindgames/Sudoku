---
applyTo: "knowledge/repo-map.md,docs/**"
---

# Repo-map audit rules

`knowledge/repo-map.md` is a generated Repomix snapshot and may become outdated.

Use it only to audit and correct curated documentation.

Regenerate `knowledge/repo-map.md` only when:
- A new Gradle module is added or removed
- The package/module structure changes significantly
- A major dependency, framework, or build-tool upgrade occurs
- A large refactor changes architecture or data flow
- Curated docs become materially inconsistent with the codebase

After regeneration:
1. Compare knowledge/repo-map.md with existing docs/*.md files.
2. Correct only factual inconsistencies.
3. Do not rewrite documentation unnecessarily.
4. Do not add knowledge/repo-map.md to always-on Copilot context.
5. Preserve concise, curated documentation.
