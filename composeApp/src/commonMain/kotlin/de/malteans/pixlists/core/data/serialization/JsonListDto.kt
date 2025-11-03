package de.malteans.pixlists.core.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonListDto(
    val name: String,
    val categories: List<JsonCategoryDto>,
    val entries: List<JsonEntryDto>,
)
