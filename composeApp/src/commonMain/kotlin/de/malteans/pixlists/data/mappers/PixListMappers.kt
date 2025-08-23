package de.malteans.pixlists.data.mappers

import de.malteans.pixlists.data.database.entities.PixListEntity
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.domain.PixList
import kotlinx.datetime.LocalDate

fun PixListEntity.toPixList(
    categories: List<PixCategory> = emptyList(),
    entries: Map<LocalDate, List<PixCategory>> = emptyMap()
): PixList {
    return PixList(
        id = id,
        name = name,
        categories = categories,
        entries = entries,
    )
}