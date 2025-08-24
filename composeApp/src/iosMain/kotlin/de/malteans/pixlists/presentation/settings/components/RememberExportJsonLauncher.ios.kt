package de.malteans.pixlists.presentation.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.serialization.json.JsonElement
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject

@Composable
actual fun rememberExportJsonLauncher(
    suggestedFileName: String,
    getData: () -> JsonElement,
    onResult: (Boolean) -> Unit,
): (jsonElement: JsonElement) -> Unit {
    val fileName = ensureJsonExtension(suggestedFileName)

    return remember(fileName) {
        { jsonElement: JsonElement ->
            val vc = topViewController()
            if (vc != null) presentExportJson(
                presentingViewController = vc,
                jsonElement = jsonElement,
                suggestedFileName = fileName,
                onResult = onResult
            )
            else onResult(false)
        }
    }
}

// Keep a strong ref to the delegate while the picker is open.
private val retainedPickerDelegates = mutableMapOf<UIDocumentPickerViewController, NSObject>()

fun presentExportJson(
    presentingViewController: UIViewController,
    jsonElement: JsonElement,
    suggestedFileName: String,
    onResult: (Boolean) -> Unit,
) {
    val fileName = ensureJsonExtension(suggestedFileName)
    val jsonString = JsonUtils.toPrettyString(jsonElement)

    // 1) Write to a temporary file
    val tempPath = NSTemporaryDirectory() + "/$fileName"
    val data = (jsonString as NSString).dataUsingEncoding(NSUTF8StringEncoding)
    requireNotNull(data) { "Failed to encode JSON to UTF-8" }
    if (!data.writeToFile(tempPath, atomically = true)) {
        onResult(false); return
    }

    val fileURL = NSURL.fileURLWithPath(tempPath)

    // 2) Present the Files "Save to..." sheet to export the file
    val picker = UIDocumentPickerViewController(forExportingURLs = listOf(fileURL), asCopy = true)

    val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
        override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
            retainedPickerDelegates.remove(controller)
            onResult(true) // exported
        }
        override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            retainedPickerDelegates.remove(controller)
            onResult(false)
        }
    }
    picker.delegate = delegate
    // retain delegate while the picker is alive
    retainedPickerDelegates[picker] = delegate

    picker.modalPresentationStyle = UIModalPresentationFullScreen
    presentingViewController.presentViewController(picker, animated = true, completion = null)
}

private fun topViewController(): UIViewController? {
    // Prefer key window from connected scenes
    val root = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .flatMap { it.windows as List<UIWindow> }
        .firstOrNull { it.isKeyWindow() }?.rootViewController
        ?: (UIApplication.sharedApplication.windows
            .firstOrNull { it is UIWindow? && it?.isKeyWindow() == true } as UIWindow?)?.rootViewController

    return unfoldTop(root)
}

private fun unfoldTop(root: UIViewController?): UIViewController? {
    var current = root ?: return null

    // Follow presentations
    while (current.presentedViewController != null) {
        current = current.presentedViewController!!
    }
    // Unwrap common containers
    when (current) {
        is UINavigationController -> return unfoldTop(current.visibleViewController)
        is UITabBarController -> return unfoldTop(current.selectedViewController)
    }
    return current
}