# FlyingWhale

[![PR Check](https://github.com/AhmetSIRIM/FlyingWhale-E-Commerce-KMP/actions/workflows/pr-check.yml/badge.svg)](https://github.com/AhmetSIRIM/FlyingWhale-E-Commerce-KMP/actions/workflows/pr-check.yml)

Kotlin Multiplatform e-commerce app, grown into an open-source dev-process showcase.

## What this is

Two threads share one repo. The first is a KMP e-commerce client across Android, iOS, web (js and wasmJs), with a Ktor backend. The second is the dev process around it: branch protection without admin bypass, an issue-first CI gate, structured issue forms, and a path toward a GitHub Actions LLM agent that triages issues without merge authority. The aim is to make the open-source practice itself part of the showcase, alongside the product code.

## Modules

| Module | Role |
|---|---|
| `app/androidApp` | Android entry point |
| `app/iosApp` | iOS entry point (Xcode project; consumes the shared framework) |
| `app/webApp` | Web entry point (Compose Multiplatform; js and wasmJs targets) |
| `app/sharedUI` | UI shared across platforms (Compose Multiplatform) |
| `app/sharedLogic` | Non-UI shared code |
| `core` | Lowest-layer shared module |
| `server` | Ktor backend |

For triage, modules group into area labels: `area:android`, `area:ios` (the Xcode app and iOS source sets, not a Gradle module), `area:web`, `area:server`, `area:shared` (covers `core`, `app/sharedLogic`, `app/sharedUI`), and `area:dev-process` for tooling.

## Stack

- Kotlin Multiplatform with Compose Multiplatform UI
- Ktor server
- Gradle 9 + Amazon Corretto 21
- detekt + Android Lint + per-platform compile in CI

## Running

- Android: `./gradlew :app:androidApp:assembleDebug`
- Server: `./gradlew :server:run`
- Web (wasm, faster): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
- Web (js, older browsers): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- iOS: open [`/app/iosApp`](./app/iosApp) in Xcode and run.

## Tests

- Android: `./gradlew :app:sharedUI:testAndroidHostTest :app:sharedLogic:testAndroidHostTest`
- Server: `./gradlew :server:test`
- Web (wasm): `./gradlew :app:sharedUI:wasmJsTest :app:sharedLogic:wasmJsTest`
- Web (js): `./gradlew :app:sharedUI:jsTest :app:sharedLogic:jsTest`
- iOS: `./gradlew :app:sharedLogic:iosSimulatorArm64Test`

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md). Every PR links an issue opened beforehand through one of the [issue forms](../../issues/new/choose); bot PRs are exempt automatically.

## Roadmap

Process maturity, in phases:

- [x] **Phase 0: Foundation.** CI gates, branch protection, issue-first PR flow, contributor surface.
- [ ] **Phase 1: LLM PM agent.** A GitHub Actions agent that triages issues and labels them, talks over Telegram, and never merges.
- [ ] **Phase 2: Security hardening.** Prompt-injection-safe agent surface, pinned action SHAs, CodeQL, SECURITY.md.

The product surface (catalog, product detail, cart, checkout) develops in parallel; short-term execution lives in the owner's working notes.

## License

[Apache-2.0](./LICENSE).
