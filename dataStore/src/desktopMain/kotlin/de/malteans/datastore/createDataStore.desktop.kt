package de.malteans.datastore

actual fun getProducePath(dataStoreConfig: DataStoreConfig): String {
    return DATA_STORE_FILE_NAME
}