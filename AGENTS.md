# AI Agent Rules for PixLists

This Kotlin Multiplatform (KMP) project leverages Compose Multiplatform to share UI and logic across Android and iOS. Follow these architectural patterns and project specific rules to ensure immediate productivity.

## Architecture & Modules

The project follows a multi-module architecture to isolate responsibilities:
- `:composeApp`: Main shared module containing Compose Multiplatform UI, central application wiring, ViewModels, and Koin DI definitions.
- `:dataStore`: Encapsulates shared preferences using Jetpack DataStore (`androidx.datastore`).
- `:legal`: A feature module housing screens for Imprint, Privacy Policy, and OSS Licenses.
- `:androidApp` & `:iosApp`: Target-specific entry points and platform configurations.

## Critical Developer Workflows

- **Updating License Information**: Whenever dependencies change, you must regenerate the Open Source License definitions file. Run the following command:
  ```shell
  ./gradlew :composeApp:exportLibraryDefinitions
  ```
  This updates `aboutlibraries.json` under `composeResources/files/`, consumed directly by the `:legal` module.

## Project-Specific Conventions

### Koin Dependency Injection
The project uses **Koin** for dependency injection. 
- Use `module { single { ... } }` templates when binding implementations.
- Platform-specific bindings are resolved through `expect/actual val platformModule: Module`. Look at `:dataStore` instructions for integration templates.

### DataStore Implementation
When adding new preferences, do not create a standalone DataStore or duplicate data storage patterns. 
- Edit `DefaultDataStoreRepository` in the `:dataStore` module.
- Add your new key strictly to either the `StringKey` or `BooleanKey` enums.
- Leverage the existing `runBlocking` helpers for synchronous fetching and `dataStore.data.map { ... }` combined flows for reactive reading.

### Navigation and Routing
- Use strongly-typed routes for Jetpack Navigation within Compose Multiplatform (`@Serializable` route objects/classes).
- Structure feature graphs into a `navigation` builder block (see `Navigation` in `:legal` README for reference). Include `enterTransition` and `popExitTransition` parameters when scaffolding new groups.

### Resource Management
- Utilize the Compose Multiplatform Resource API (`Res`).
- Put specific file assets (like `.html` and `.json`) in `composeResources/files/`.
- Ensure files are read correctly in a background thread if large (e.g. `Res.readBytes`), usually housed inside `LaunchedEffect` wrappers or external repository layers.
