# Copilot Instructions

This repository uses `AGENTS.md` as the main project guidance file for AI coding agents.

## Primary guidance
- Read `AGENTS.md` first for repository purpose, build conventions, and development notes.
- Read `app-workflow.skill.md` for repo-specific Android Compose and Gemini API workflow guidance.

## What to know
- This is a single-module Android app in `app/`.
- The app is Compose-first and uses `MainActivity` to render `MainScreen(viewModel = viewModel)`.
- `LauncherViewModel` manages state, chat assistant flow, and item persistence through Room.
- `GeminiRepository` uses `BuildConfig.GEMINI_API_KEY` and the Gradle Secrets plugin with `.env`.

## Build and test
- Use `gradle clean assembleDebug`.
- Use `gradle test`.
- Use `gradle connectedAndroidTest` when a device or emulator is available.
- There is no Gradle wrapper; use the installed `gradle` command.

## Notes
- Do not duplicate the design documentation in `docs/`; link to `docs/DESIGN_PHILOSOPHY.md` and `docs/SPECTRAL_LAYERS.md` instead.
- Preserve the repository's existing aesthetic and Compose conventions.
