package de.malteans.pixlists.core.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonEntryDto(
    val epochDays: Long,
    val categoryNames: List<String>,
    val categoryWeights: List<Int?> = emptyList()
)
