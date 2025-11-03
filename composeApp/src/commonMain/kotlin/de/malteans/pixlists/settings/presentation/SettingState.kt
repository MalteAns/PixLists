package de.malteans.pixlists.settings.presentation

import kotlinx.serialization.json.JsonElement

data class SettingState (
    val exportInProgress: Boolean = false,
    val importInProgress: Boolean = false,

    val exportData: JsonElement? = null,
    val importSuccess: Boolean = false,
    val importError: String? = null,
)
