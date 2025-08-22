package de.malteans.pixlists.presentation.settings

import de.malteans.pixlists.app.Route

sealed interface SettingsAction {
    data object OpenDrawer : SettingsAction
    data class OnNavigateTo(val route: Route) : SettingsAction
}