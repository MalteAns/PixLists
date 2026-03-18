package de.malteans.pixlists.core.presentation.util

import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate

fun Map<LocalDate, List<PixCategory>>.getAbsolutMap(
    categories: List<PixCategory>, dateRange: Pair<LocalDate?, LocalDate?>? = null
): Map<PixCategory, Int> {
    val absoluteCountMap = categories.associateWith { 0 }.toMutableMap()

    for ((date, entryCategories) in entries) {
        if (dateRange != null) {
            val (startDate, endDate) = dateRange
            if ((startDate != null && date < startDate) || (endDate != null && date > endDate)) {
                continue
            }
        }

        for (category in categories) {
            if (entryCategories.contains(category)) {
                absoluteCountMap[category] = (absoluteCountMap[category] ?: 0) + 1
            }
        }
    }
    return absoluteCountMap
}

fun Map<PixCategory, Int>.toRelativeMap(): Map<PixCategory, Double> {
    val totalCount = values.sumOf { it }
    return this.mapValues { (_, absCount) ->
        if (totalCount > 0) absCount.toDouble() / totalCount else 0.0
    }
}