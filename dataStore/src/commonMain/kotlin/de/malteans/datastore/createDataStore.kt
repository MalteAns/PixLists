package de.malteans.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

expect fun getProducePath(dataStoreConfig: DataStoreConfig): String

fun createDataStore(dataStoreConfig: DataStoreConfig): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { getProducePath(dataStoreConfig).toPath() }
    )
}

data class DataStoreConfig(
    val androidContext: Any?,
)

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"