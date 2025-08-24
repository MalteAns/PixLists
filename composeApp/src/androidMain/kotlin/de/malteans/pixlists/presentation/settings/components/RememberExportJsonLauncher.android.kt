package de.malteans.pixlists.presentation.settings.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.json.JsonElement

private const val TAG = "rememberExportJsonLauncher"

@Composable
actual fun rememberExportJsonLauncher(
    suggestedFileName: String,
    getData: () -> JsonElement,
    onResult: (Boolean) -> Unit,
): (jsonElement: JsonElement) -> Unit {
    val context = LocalContext.current
    val fileName = ensureJsonExtension(suggestedFileName)

    val createDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val jsonElement = try {
            getData()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error getting data to export", e)
            onResult(false)
            return@rememberLauncherForActivityResult
        }
        if (uri == null){
            Log.e(TAG, "URI is null")
            onResult(false)
        } else onResult(context.writeJsonToUri(uri, jsonElement))
    }

    return remember {
        { jsonElement: JsonElement ->
            createDocument.launch(fileName)
        }
    }
}

private fun Context.writeJsonToUri(uri: Uri, json: JsonElement): Boolean {
    return try {
        val bytes = JsonUtils.toPrettyString(json).toByteArray(Charsets.UTF_8)
        contentResolver.openOutputStream(uri)?.use { out ->
            out.write(bytes); out.flush()
        } ?: run {
            Log.e(TAG, "Failed to open output stream for URI: $uri")
            return false
        }
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error writing JSON to URI", e)
        false
    }
}