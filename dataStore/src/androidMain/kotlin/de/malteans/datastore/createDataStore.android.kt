package de.malteans.datastore

import android.content.Context

actual fun getProducePath(dataStoreConfig: DataStoreConfig): String {
    val context = dataStoreConfig.androidContext as Context
    return context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
}