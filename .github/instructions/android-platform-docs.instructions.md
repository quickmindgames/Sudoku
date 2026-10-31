---
applyTo: "app/src/main/AndroidManifest.xml,app/src/main/res/**,gradle/**,build.gradle*,settings.gradle*,docs/architecture.md,docs/onboarding.md,docs/pitfalls.md"
---

# Android platform and environment documentation

When changing AndroidManifest configuration, permissions, deep links, push notifications, storage, services, receivers, Gradle modules, build variants, signing, ProGuard/R8, or release workflow:

1. Update docs/onboarding.md if setup, build, run, or release steps change.
2. Update docs/architecture.md if platform architecture or native integration changes.
3. Update docs/pitfalls.md if the change introduces a platform-specific risk.
4. Never document or expose secrets, signing credentials, API keys, tokens, or environment values.
5. Clearly distinguish Android-only, iOS-only, and cross-platform behavior when relevant.
