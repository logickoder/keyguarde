# Contributing to Keyguarde

Thanks for your interest in contributing to Keyguarde! Whether you're here to file a bug, suggest an idea, or submit a pull request, you're very welcome.

## How to Contribute

### 1. Fork the repository

First, fork the repository to your own GitHub account and clone it locally:

```bash
git clone https://github.com/YOUR_USERNAME/keyguarde.git
cd keyguarde
```

### 2. Create a new branch

Always create a new branch from `main` for your work:

```bash
git checkout -b your-feature-name
```

### 3. Make your changes

Make your improvements, fix bugs, or add new features.

Ensure your code:

* Follows Kotlin best practices
* Uses Jetpack Compose conventions
* Targets minimum SDK 23+
* Avoids hardcoded strings (use resources when possible)

### 4. Run the linter & tests

Use Android Studio to:

* Run the app and test your changes on a physical/emulator device.
* Run `Code > Reformat Code` and `Analyze > Inspect Code` to clean up and catch any issues.

### 5. Commit and push

Commit with a clear and descriptive message:

```bash
git commit -m "Add: your feature or fix"
git push origin your-feature-name
```

### 6. Open a pull request

Submit your pull request to the main repo via GitHub. Be sure to include:

* A description of what you changed
* Any related issue number (e.g., `Fixes #12`)
* Screenshots or screen recordings if your change affects the UI

## Code Style Guidelines

We follow standard Kotlin style conventions. Use Android Studio's formatting tools or install `ktlint`.

## Reporting Issues

If you found a bug or have a feature request:

* Search the [issues](https://github.com/logickoder/keyguarde/issues) first to avoid duplicates.
* If none exists, open a new issue with a clear description and steps to reproduce (if applicable).

## Discussions

Not ready to contribute code? That’s fine too!

* Join our [GitHub Discussions](https://github.com/logickoder/keyguarde/discussions) to suggest ideas, provide feedback, or ask questions.

## Thank You!

Your help makes Keyguarde better for everyone. We appreciate every contribution — big or small.