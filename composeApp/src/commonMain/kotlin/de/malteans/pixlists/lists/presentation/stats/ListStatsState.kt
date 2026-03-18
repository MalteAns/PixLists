package de.malteans.pixlists.lists.presentation.stats

import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate

data class ListStatsState(
    val allCategories: List<PixCategory> = emptyList(),

    val selectedCategories: List<PixCategory> = emptyList(),
    val dateRange: Pair<LocalDate?, LocalDate?>? = null,

    val absoluteMap: Map<PixCategory, Int> = emptyMap(),
    val relativeMap: Map<PixCategory, Double> = emptyMap(),
)
