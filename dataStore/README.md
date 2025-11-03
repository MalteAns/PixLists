## Usage
### 1. DataStore<Preferences> in DI Module
Shared module template:
```kotlin
val sharedModule = module {
    single<DataStore<Preferences>> { createDataStore(get()) }
    
    single<DataStoreRepository> { DefaultDataStoreRepository(get()) }
}
```
Plattform-specific module template (Android):
```kotlin
actual val platformModule: Module
    get() = module {
        single<DataStoreConfig> { DataStoreConfig(androidApplication()) }
    }
```
Plattform-specific module template (other):
```kotlin
actual val platformModule: Module
    get() = module {
        single<DataStoreConfig> { DataStoreConfig(null) }
    }
```
### 2. Create DataStoreRepository
Template for implementation:
```kotlin
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DefaultDataStoreRepository(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {
    private enum class StringKey(val prefKey: Preferences.Key<String>) {
        
    }
    private enum class BooleanKey(val prefKey: Preferences.Key<Boolean>) {
        
    }


    private fun getString(key: StringKey): String? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getStringFlow(key: StringKey, default: String? = null): Flow<String?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveString(key: StringKey, value: String?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }

    private fun getBoolean(key: BooleanKey): Boolean? = runBlocking {
        dataStore.data.firstOrNull()?.get(key.prefKey)
    }
    private fun getBooleanFlow(key: BooleanKey, default: Boolean? = null): Flow<Boolean?> =
        dataStore.data.map { it[key.prefKey] ?: default }
    private suspend fun saveBoolean(key: BooleanKey, value: Boolean?) {
        dataStore.edit { prefs ->
            if (value == null) prefs.remove(key.prefKey)
            else prefs[key.prefKey] = value
        }
    }
}
```

## Dependencies:
### VersionCatalog:
```toml
agp = "8.12.3"
kotlin = "2.2.20"
compose-multiplatform = "1.9.2"
androidx-activity = "1.11.0"

kotlinStdlib = "2.2.20"

datastore = "1.1.7"

[libraries]
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "androidx-activity" }

kotlin-stdlib = { group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version.ref = "kotlinStdlib" }

datastore = { module = "androidx.datastore:datastore", version.ref = "datastore" }
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }

[plugins]
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
android-kotlin-multiplatform-library = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
compose-multiplatform = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### root build.gradle.kts
```kts
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}
```

### app build.gradle.kts
```kts
[...]
kotlin {
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(projects.dataStore)
            [...]
        }
        commonMain.dependencies {
            implementation(projects.dataStore)
            [...]
        }
        iosMain.dependencies {
            implementation(projects.dataStore)
            [...]
        }
        desktopMain.dependencies {
            implementation(projects.dataStore)
            [...]
        }
    }
}
```