package de.malteans.pixlists.presentation.settings.components

import androidx.compose.runtime.Composable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

// lenient parser is handy for imports
@OptIn(ExperimentalSerializationApi::class)
val ImportJson by lazy {
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowTrailingComma = true
    }
}

@Composable
expect fun rememberImportJsonLauncher(
    onResult: (Result<JsonElement>) -> Unit
): () -> Unit
