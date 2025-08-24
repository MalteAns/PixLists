package de.malteans.pixlists.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonColorDto(
    val name: String,
    val red: Float,
    val green: Float,
    val blue: Float,
)