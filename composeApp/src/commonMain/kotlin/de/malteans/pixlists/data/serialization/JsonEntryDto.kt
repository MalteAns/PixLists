package de.malteans.pixlists.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonEntryDto(
    val epochDays: Long,
    val categoryNames: List<String>
)
