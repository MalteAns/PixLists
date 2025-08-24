package de.malteans.pixlists.presentation.settings

import kotlinx.serialization.json.JsonElement

data class SettingState (
    val isLoading: Boolean = false,

    val exportData: JsonElement? = null,
    val importError: String? = null,
)
