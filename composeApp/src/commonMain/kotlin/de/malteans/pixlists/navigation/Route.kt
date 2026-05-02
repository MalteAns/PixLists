package de.malteans.pixlists.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Dashboard : Route {
        @Serializable
        data object View : Route
        @Serializable
        data object Edit : Route
    }

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
    data object Colors : Route {
        @Serializable
        data object Overview : Route
    }

    @Serializable
    data object Settings : Route {
        @Serializable
        data object Overview : Route
    }

    @Serializable
    data object Legal : Route
}