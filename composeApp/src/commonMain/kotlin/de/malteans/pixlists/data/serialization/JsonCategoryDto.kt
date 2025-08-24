package de.malteans.pixlists.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonCategoryDto (
    val name: String,
    val colorName: String,
    val orderIndex: Int,
)
