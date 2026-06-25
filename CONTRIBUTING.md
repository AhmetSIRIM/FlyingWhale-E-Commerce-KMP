# Contributing

Thanks for the interest. This guide covers how work flows here.

## Claim an issue first

Open a [new issue](../../issues/new/choose) via one of the forms before writing code. If you want to take an existing one, comment to claim it; a maintainer assigns you as a coordination signal, not as a permission gate. New ideas are welcome through the feature proposal form.

For tiny fixes (typos, broken links, obvious one-liners), the same rule applies; the bug form takes about a minute and keeps the issue/PR trail consistent for the project's tooling.

## Open a PR that closes the issue

Fork the repo, branch off `main`, and reference the issue in your PR description with a closing keyword:

```
Closes #123
```

The PR template prompts for this. The `require-linked-issue` check enforces it and re-runs automatically when you edit the PR description; no new commit needed.

Bot PRs (Renovate, Dependabot) are exempt automatically.

## Pre-push (kept light)

Before pushing, run:

```
./gradlew detektAll
```

Compile the modules you touched (for example `./gradlew :core:compileKotlinJvm`). Skip the full assemble locally; CI runs it across every platform and reports faster than your laptop will.

## CI gates

Two required checks block merge:

- **`ci-gate`** joins the platform matrix (android, jvm, web-js, web-wasm, ios), Android Lint, and Detekt. A failure names the platform that broke.
- **`require-linked-issue`** asserts the PR description carries a closing keyword.

A red platform leg is the fastest way to debug; open its log directly, not the parent run.

## First contribution from a fork

GitHub holds workflow runs for first-time contributors until a maintainer approves them. This is GitHub's default, not a project policy; after your first approved PR, the gate goes away. If `ci-gate` and `require-linked-issue` show "Workflows awaiting approval", that is what is happening.

## Code style

Detekt config (`config/detekt/detekt.yml`) is the source of truth. Comments are English, concise, and only where the reason isn't already in the code; tests are the one place generous comments are welcome.
