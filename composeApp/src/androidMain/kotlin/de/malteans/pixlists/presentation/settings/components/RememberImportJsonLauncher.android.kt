package de.malteans.pixlists.presentation.settings.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.json.JsonElement
import kotlin.coroutines.cancellation.CancellationException

@Composable
actual fun rememberImportJsonLauncher(
    onResult: (Result<JsonElement>) -> Unit
): () -> Unit {
    val context = LocalContext.current

    // Restrict to JSON-ish MIME types (users can still browse Files app)
    val openDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) {
            onResult(Result.failure(CancellationException("User cancelled")))
            return@rememberLauncherForActivityResult
        }
        try {
            val text = context.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
                ?: error("Unable to read selected file")
            val element = ImportJson.parseToJsonElement(text)
            onResult(Result.success(element))
        } catch (t: Throwable) {
            onResult(Result.failure(t))
        }
    }

    return remember {
        {
            // You can pass multiple types; this keeps the sheet focused
            openDoc.launch(arrayOf("application/json", "text/json", "application/*+json"))
        }
    }
}