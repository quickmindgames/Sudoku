---
applyTo: "app/src/main/**,feature-*/**,core/**,docs/architecture.md,docs/module-map.md,docs/data-flow.md"
---

# Navigation and screen documentation

When adding or changing an Activity, Fragment, Compose screen, navigation route, tab, modal, or deep link:

1. Check docs/module-map.md for affected modules.
2. Update docs/data-flow.md if the user journey or navigation flow changes.
3. Update docs/architecture.md only if navigation architecture changes.
4. Document screen purpose, entry point, navigation arguments, and major data dependencies.
5. Do not duplicate implementation code in documentation.
