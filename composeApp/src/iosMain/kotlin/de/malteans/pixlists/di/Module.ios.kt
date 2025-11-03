package de.malteans.pixlists.di

import de.malteans.datastore.DataStoreConfig
import de.malteans.pixlists.core.data.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<DataStoreConfig> { DataStoreConfig(null) }
        single { DatabaseFactory() }
    }