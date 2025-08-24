package de.malteans.pixlists.presentation.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.JsonElement
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.stringWithContentsOfURL
import platform.UIKit.*
import platform.darwin.NSObject

private val retainedDelegates = mutableMapOf<UIDocumentPickerViewController, NSObject>()

@Composable
actual fun rememberImportJsonLauncher(
    onResult: (Result<JsonElement>) -> Unit
): () -> Unit {
    return remember {
        {
            val vc = topViewController() ?: run {
                onResult(Result.failure(IllegalStateException("No visible UIViewController")))
                return@remember
            }

            // Filter for JSON. Fallbacks included to make browsing easier.
            // Uses the (ObjC) documentTypes API for broad compatibility.
            val types = listOf("public.json", "public.text", "public.item")
            val picker = UIDocumentPickerViewController(
                documentTypes = types,
                inMode = UIDocumentPickerModeImport
            )

            val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                @OptIn(ExperimentalForeignApi::class)
                override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                    retainedDelegates.remove(controller)

                    val url = (didPickDocumentsAtURLs.firstOrNull() as? NSURL)
                    if (url == null) {
                        onResult(Result.failure(CancellationException("No file chosen")))
                        return
                    }

                    val scoped = url.startAccessingSecurityScopedResource()
                    try {
                        // Read UTF‑8 (fallback to NSData if needed)
                        val text: String? = try {
                            NSString.stringWithContentsOfURL(url, NSUTF8StringEncoding, null) as String?
                        } catch (_: Throwable) {
                            (NSData.dataWithContentsOfURL(url))?.let { data ->
                                // last‑resort decode
                                NSString.create(data, NSUTF8StringEncoding) as String?
                            }
                        }

                        if (text == null) error("Failed to read file")
                        val element = ImportJson.parseToJsonElement(text)
                        onResult(Result.success(element))
                    } catch (t: Throwable) {
                        onResult(Result.failure(t))
                    } finally {
                        if (scoped) url.stopAccessingSecurityScopedResource()
                    }
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    retainedDelegates.remove(controller)
                    onResult(Result.failure(CancellationException("User cancelled")))
                }
            }

            picker.delegate = delegate
            retainedDelegates[picker] = delegate
            picker.modalPresentationStyle = UIModalPresentationFullScreen
            vc.presentViewController(picker, animated = true, completion = null)
        }
    }
}

// Same helper as before; finds the top-most VC
private fun topViewController(): UIViewController? {
    val root = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .flatMap { it.windows as List<UIWindow> }
        .firstOrNull { it.isKeyWindow }?.rootViewController
        ?: UIApplication.sharedApplication.windows.firstOrNull { it.isKeyWindow }?.rootViewController

    return unfoldTop(root)
}

private fun unfoldTop(root: UIViewController?): UIViewController? {
    var current = root ?: return null
    while (current.presentedViewController != null) current = current.presentedViewController!!
    when (current) {
        is UINavigationController -> return unfoldTop(current.visibleViewController)
        is UITabBarController     -> return unfoldTop(current.selectedViewController)
    }
    return current
}
