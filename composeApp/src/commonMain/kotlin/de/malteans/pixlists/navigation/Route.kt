package de.malteans.pixlists.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object ListNav : Route
    @Serializable
    sealed interface List : Route {
        @Serializable
        data object Loading : List
        @Serializable
        data class View(
            val curPixListId: Long? = null,
        ) : List
    }

    @Serializable
    data object ColorsNav : Route
    @Serializable
    sealed interface Colors : Route {
        @Serializable
        data object Overview : Colors
    }

    @Serializable
    data object SettingsNav : Route
    @Serializable
    sealed interface Settings : Route {
        @Serializable
        data object Overview : Settings
    }

    @Serializable
    data object LegalNav : Route
}