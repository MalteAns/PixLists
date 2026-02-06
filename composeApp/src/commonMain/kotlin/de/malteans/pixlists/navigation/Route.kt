package de.malteans.pixlists.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object List : Route {
        @Serializable
        data object Loading : Route
        @Serializable
        data class View(
            val curPixListId: Long? = null,
        ) : Route
        @Serializable
        data class Stats(
            val listId: Long,
        ) : Route
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