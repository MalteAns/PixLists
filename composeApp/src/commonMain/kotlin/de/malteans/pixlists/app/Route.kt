package de.malteans.pixlists.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data class ListScreen(
        val curPixListId: Long? = null,
    ) : Route

    @Serializable
    object ManageColorsScreen : Route

    @Serializable
    object LoadingScreen : Route

    @Serializable
    object SettingsScreen : Route

    @Serializable
    object LicensesScreen : Route
}