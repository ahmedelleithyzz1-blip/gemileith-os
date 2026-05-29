# AGENTS

## Purpose
Help AI coding agents understand the Gemileith OS repository quickly and work safely with its Android Compose app, design docs, and build conventions.

## What this repo is
- Android Jetpack Compose app in `app/`.
- Uses Room, Retrofit, Moshi, Compose Material3, and a custom AI/chat UI.
- Includes rich design documentation in `docs/` and case-study examples in `case-studies/`.
- No Gradle wrapper files are present; the repo expects a system Gradle installation.

## Key directories
- `app/` — main Android module, app source, resources, and Gradle config.
- `app/src/main/java/com/example/` — Compose UI, `MainActivity`, view model, repository, data layer.
- `app/src/main/res/` — layout assets, themes, strings, icons, XML rules.
- `docs/` — architecture and design philosophy documentation.
- `case-studies/` — visual design examples for WhatsApp, Camera, Settings.
- `metadata.json` — repo metadata and declared capabilities.

## Important files
- `app/build.gradle.kts` — build configuration, plugin list, dependencies, secrets plugin setup.
- `settings.gradle.kts` — single-module project, module included is `:app`.
- `README.md` — high-level design philosophy and project overview.
- `metadata.json` — describes the project identity and AI/Gemini capability.

## Build and test commands
- `gradle clean assembleDebug`
- `gradle test`
- `gradle connectedAndroidTest`
- `gradle app:dependencies`
- `gradle -q tasks` if you need available task names.

> Note: There is no `gradlew` or `gradlew.bat` in this repository, so use the locally installed `gradle` command.

## Project conventions
- The Android app is Compose-first and uses `MainActivity` to host `MainScreen(viewModel = viewModel)`.
- UI state flows through `LauncherViewModel` and `LauncherRepository`.
- `GEMINI_API_KEY` is injected as a Gradle build config field in `app/build.gradle.kts` and resolves from project properties or falls back to a placeholder.
- Secrets are expected to come from `.env` using the Gradle Secrets plugin; `.env.example` is present as a template.

## Documentation guidance for agents
- Do not duplicate the design philosophy from `docs/`; link to it instead.
- Use `docs/DESIGN_PHILOSOPHY.md` and `docs/SPECTRAL_LAYERS.md` for visual and concept guidance.
- Refer to `case-studies/` for example conventions and style patterns.

## Best approach for changes
- Prefer edits in `app/` for runtime behavior and UI features.
- Preserve the existing design system language and theme conventions.
- Keep Android package naming and Compose conventions consistent with `com.example` sources.

## Skills
- See `app-workflow.skill.md` for repo-specific Android Compose and Gemini workflow guidance.
