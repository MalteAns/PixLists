package de.malteans.pixlists.presentation.settings

import de.malteans.pixlists.app.Route
import kotlinx.serialization.json.JsonElement

sealed interface SettingsAction {
    data object OpenDrawer : SettingsAction
    data class OnNavigateTo(val route: Route) : SettingsAction

    data object OnExportData : SettingsAction
    data object ResetExportData : SettingsAction
    data class OnImportData(val data: JsonElement) : SettingsAction
    data object ClearImportError : SettingsAction
}