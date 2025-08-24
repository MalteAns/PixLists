package de.malteans.pixlists.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonListDto(
    val name: String,
    val categories: List<JsonCategoryDto>,
    val entries: List<JsonEntryDto>,
)
