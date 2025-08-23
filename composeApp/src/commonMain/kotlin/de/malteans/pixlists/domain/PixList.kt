package de.malteans.pixlists.domain

import kotlinx.datetime.LocalDate

data class PixList(
    val id: Long,
    val name: String,
    val categories: List<PixCategory>,
    val entries: Map<LocalDate, List<PixCategory>>,
)
