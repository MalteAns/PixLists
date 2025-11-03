package de.malteans.pixlists.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.malteans.datastore.createDataStore
import de.malteans.pixlists.colors.presentation.ManageColorsViewModel
import de.malteans.pixlists.core.data.DefaultDataStoreRepository
import de.malteans.pixlists.core.data.database.DatabaseFactory
import de.malteans.pixlists.core.data.database.PixDatabase
import de.malteans.pixlists.core.data.repository.DefaultPixRepository
import de.malteans.pixlists.core.domain.DataStoreRepository
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.core.presentation.main.MainViewModel
import de.malteans.pixlists.lists.presentation.ListViewModel
import de.malteans.pixlists.settings.presentation.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<DataStore<Preferences>> { createDataStore(get()) }

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<PixDatabase>().pixDao }

    single<DataStoreRepository> { DefaultDataStoreRepository(get()) }
    single<PixRepository> { DefaultPixRepository(get()) }

    viewModelOf(::MainViewModel)
    viewModelOf(::ListViewModel)
    viewModelOf(::ManageColorsViewModel)
    viewModelOf(::SettingsViewModel)
}