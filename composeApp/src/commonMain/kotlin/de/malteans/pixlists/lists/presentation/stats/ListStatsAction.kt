package de.malteans.pixlists.lists.presentation.stats

import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate

sealed interface ListStatsAction {
    data object NavigateBack: ListStatsAction

    data class IncludeCategory(val category: PixCategory?, val include: Boolean): ListStatsAction

    data class SetDateRange(val startDate: LocalDate?, val endDate: LocalDate?): ListStatsAction
}