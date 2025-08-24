package de.malteans.pixlists.data.serialization

import kotlinx.serialization.Serializable

@Serializable
data class JsonFullDataDto(
    val colors: List<JsonColorDto>,
    val lists: List<JsonListDto>,
)