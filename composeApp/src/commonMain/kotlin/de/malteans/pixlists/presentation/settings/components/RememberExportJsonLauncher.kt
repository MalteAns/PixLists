package de.malteans.pixlists.presentation.settings.components

import androidx.compose.runtime.Composable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Composable
expect fun rememberExportJsonLauncher(
    suggestedFileName: String,
    getData: () -> JsonElement,
    onResult: (Boolean) -> Unit,
): (jsonElement: JsonElement) -> Unit

object JsonUtils {
    private val prettyJson = Json { prettyPrint = true }

    fun toPrettyString(element: JsonElement): String =
        prettyJson.encodeToString(JsonElement.serializer(), element)
}

fun ensureJsonExtension(name: String): String =
    if (name.lowercase().endsWith(".json")) name else "$name.json"