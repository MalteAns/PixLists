package de.malteans.pixlists.lists.presentation.stats

import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate

data class ListStatsState(
    val isLoading: Boolean = true,

    val allCategories: List<PixCategory> = emptyList(),

    val dateRange: Pair<LocalDate?, LocalDate?>? = null,

    /** <Category (null for no category), <absolute count, relative count>> */
    val statsMap: Map<PixCategory?, Pair<Int, Double>> = emptyMap(),
)
