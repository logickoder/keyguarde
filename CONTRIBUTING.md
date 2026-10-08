# Contributing to Keyguarde

Thanks for helping. Bug reports, ideas and pull requests are all welcome.

## Setup

1. Fork the repo and clone your fork:

   ```bash
   git clone https://github.com/YOUR_USERNAME/keyguarde.git
   cd keyguarde
   ```

2. Add `app/google-services.json` from your own Firebase project. The README explains which app IDs it needs.
3. Open the project in Android Studio with JDK 17.

## Making a change

1. Branch from `main`:

   ```bash
   git checkout -b your-change
   ```

2. Keep the code in line with the rest of the app:
   - Kotlin and Jetpack Compose. Each screen has an outer composable that talks to the ViewModel and an inner one that takes state and callbacks.
   - Minimum SDK 26.
   - User-facing text goes in `strings.xml`.
   - Colours, spacing and type come from the theme. See [THEMING.md](THEMING.md).
   - Analytics events never carry message text, chat names or keywords.

3. Run the checks before you push:

   ```bash
   ./gradlew :app:lintDebug :app:testDebugUnitTest
   ```

   For website changes, run `pnpm lint` and `pnpm build` in `website/`.

4. Commit using [Conventional Commits](https://www.conventionalcommits.org/), for example `fix(home): keep new matches in view`.

## Pull requests

Include:

- What changed and why.
- The issue it fixes, if any (`Fixes #12`).
- Screenshots or a recording for visible changes, in light and dark mode.

## Issues and ideas

- Search [issues](https://github.com/logickoder/keyguarde/issues) first to avoid duplicates.
- Bugs: say what you did, what you expected, and what happened. Include your phone model and Android version.
- Questions and ideas: [Discussions](https://github.com/logickoder/keyguarde/discussions).
