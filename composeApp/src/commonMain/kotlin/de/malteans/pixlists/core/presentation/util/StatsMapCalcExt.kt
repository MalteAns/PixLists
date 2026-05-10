package de.malteans.pixlists.core.presentation.util

import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixEntry
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun Map<LocalDate, List<PixEntry>>.getAbsoluteMap(
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
            if (entryCategories.any { it.category == category }) {
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

fun Map<LocalDate, List<PixEntry>>.getLineChartData(
    categories: List<PixCategory>
): Map<PixCategory, List<Pair<LocalDate, Int>>> {
    if (categories.isEmpty() || this.isEmpty()) return emptyMap()

    val result = mutableMapOf<PixCategory, MutableList<Pair<LocalDate, Int>>>()

    val sortedEntries = this.entries.sortedBy { it.key }
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val lastDate = maxOf(sortedEntries.last().key, currentDate)

    categories.forEach { category ->
        val currentList = mutableListOf<Pair<LocalDate, Int>>()
        result[category] = currentList

        sortedEntries.forEach { (date, entryCategories) ->
            if (entryCategories.any { it.category == category }) {
                val nextCount = (currentList.lastOrNull()?.second ?: 0) + 1
                currentList.add(date to nextCount)
            }
        }

        currentList.add(lastDate to (currentList.lastOrNull()?.second ?: 0))
    }

    return result.mapValues { (_, values) -> values.toList() }
}