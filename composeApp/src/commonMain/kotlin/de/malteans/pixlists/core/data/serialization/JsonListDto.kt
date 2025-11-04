package de.malteans.pixlists.core.data.serialization

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class JsonListDto(
    val name: String,
    val categories: List<JsonCategoryDto>,
    val entries: List<JsonEntryDto>,
    val years: List<Int> = listOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year),
)
