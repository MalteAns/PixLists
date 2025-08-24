package de.malteans.pixlists.data.mappers

import de.malteans.pixlists.data.database.entities.PixListEntity
import de.malteans.pixlists.data.serialization.JsonEntryDto
import de.malteans.pixlists.data.serialization.JsonListDto
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

fun PixList.toJsonDto(): JsonListDto {
    return JsonListDto(
        name = this.name,
        categories = this.categories.map { it.toJsonDto() },
        entries = this.entries.map { (date, categories) ->
            JsonEntryDto(
                epochDays = date.toEpochDays(),
                categoryNames = categories.map { it.name }
            )
        }
    )
}

fun JsonListDto.toEntity(id: Long = 0L): PixListEntity {
    return PixListEntity(
        id = id,
        name = this.name,
    )
}