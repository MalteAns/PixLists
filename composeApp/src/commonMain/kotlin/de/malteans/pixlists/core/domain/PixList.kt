package de.malteans.pixlists.core.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class PixList(
    val id: Long,
    val name: String,
    val categories: List<PixCategory>,
    val entries: Map<LocalDate, List<PixEntry>>,
    val years: List<Int> = listOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year),
)
