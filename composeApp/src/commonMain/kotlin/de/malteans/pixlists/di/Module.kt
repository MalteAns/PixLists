package de.malteans.pixlists.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.malteans.pixlists.data.database.DatabaseFactory
import de.malteans.pixlists.data.database.PixDatabase
import de.malteans.pixlists.data.repository.DefaultPixRepository
import de.malteans.pixlists.domain.PixRepository
import de.malteans.pixlists.presentation.list.ListViewModel
import de.malteans.pixlists.presentation.main.MainViewModel
import de.malteans.pixlists.presentation.manageColors.ManageColorsViewModel
import de.malteans.pixlists.presentation.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<PixDatabase>().pixDao }

    single<PixRepository> { DefaultPixRepository(get()) }

    viewModel { MainViewModel(get()) }
    viewModel { ListViewModel(get()) }
    viewModel { ManageColorsViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}