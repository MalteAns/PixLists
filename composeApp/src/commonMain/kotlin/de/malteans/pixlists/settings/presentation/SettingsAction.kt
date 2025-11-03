package de.malteans.pixlists.settings.presentation

import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.pixlists.navigation.Route
import kotlinx.serialization.json.JsonElement

sealed interface SettingsAction {
    data object OpenDrawer : SettingsAction
    data class OnNavigateTo(val route: Route) : SettingsAction
    data class OnNavigateToLegalRoute(val legalRoute: LegalRoute) : SettingsAction

    data object OnExportData : SettingsAction
    data object ResetExportData : SettingsAction
    data class OnImportData(val data: JsonElement) : SettingsAction
    data object ClearImportFeedback : SettingsAction
}