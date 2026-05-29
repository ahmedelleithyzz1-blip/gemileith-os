# App Workflow Skill

## Purpose
Describe repository-specific Android Compose, Gemini API, and launcher workflow conventions for Gemileith OS.

## When to use
- When changing UI behavior in `app/src/main/java/com/example/ui/`
- When modifying state, navigation, or item persistence in `LauncherViewModel.kt` and `LauncherRepository.kt`
- When updating Gemini integration, Retrofit, or API key handling in `app/src/main/java/com/example/ai/`
- When working on design-driven launcher features, icon/filter style logic, or custom app item creation

## Build and test workflow
- Use `gradle clean assembleDebug` to build the app.
- Use `gradle test` for unit and instrumentation test execution.
- Use `gradle connectedAndroidTest` for Android integration tests if device/emulator is available.
- Use `gradle app:dependencies` to inspect modules and imported libraries.
- Use `gradle -q tasks` to list available Gradle tasks.

## Key architecture notes
- `MainActivity.kt` hosts a Compose tree and renders `MainScreen(viewModel = viewModel)`.
- `LauncherViewModel` is the app's primary state holder; it manages:
  - `allItems` from `LauncherRepository`
  - `chatMessages` and Gemini chat flow
  - selected item state and pending transforms
  - backdrop and glow multiplier state
- `LauncherRepository` wraps `LauncherItemDao` and exposes item CRUD operations.

## Gemini integration
- `GeminiRepository` uses `BuildConfig.GEMINI_API_KEY`.
- `app/build.gradle.kts` injects `GEMINI_API_KEY` from project properties or fallback value `MY_GEMINI_API_KEY`.
- The repo uses the Gradle Secrets plugin with `.env` and `.env.example`.
- If the key is missing or placeholder, the app enters offline mode and returns a local warning message.

## Developer conventions
- Preserve the Compose-first pattern and `com.example` package naming.
- Keep UI theming aligned with the existing cosmic/neon aesthetic.
- Avoid duplicating design philosophy from `docs/`; instead refer to:
  - `docs/DESIGN_PHILOSOPHY.md`
  - `docs/SPECTRAL_LAYERS.md`
- Prefer edits in `app/` for runtime and UI behavior changes.

## Notes for AI agents
- Focus on the launcher app's custom icon/state management and chat assistant behavior.
- Do not add a Gradle wrapper; the project intentionally uses the system `gradle` command.
- Respect the repo's existing scope: a single `:app` module, Compose UI, Room persistence, Retrofit/Moshi networking.
