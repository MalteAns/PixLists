package de.malteans.pixlists.lists.presentation.stats

import kotlinx.datetime.LocalDate

sealed interface ListStatsAction {
    data object NavigateBack: ListStatsAction

    data class SetCategorySelected(val categoryId: Long, val selected: Boolean): ListStatsAction
    data class SetAllCategoriesSelected(val selected: Boolean): ListStatsAction

    data class SetDateRange(val startDate: LocalDate?, val endDate: LocalDate?): ListStatsAction
}