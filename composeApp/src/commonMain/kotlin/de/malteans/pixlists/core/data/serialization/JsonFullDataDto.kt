package de.malteans.pixlists.core.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonFullDataDto(
    val colors: List<JsonColorDto>,
    val lists: List<JsonListDto>,
)