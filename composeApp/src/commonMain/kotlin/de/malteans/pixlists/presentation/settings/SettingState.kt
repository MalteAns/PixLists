package de.malteans.pixlists.presentation.settings

import kotlinx.serialization.json.JsonElement

data class SettingState (
    val exportInProgress: Boolean = false,
    val importInProgress: Boolean = false,

    val exportData: JsonElement? = null,
    val importSuccess: Boolean = false,
    val importError: String? = null,
)
