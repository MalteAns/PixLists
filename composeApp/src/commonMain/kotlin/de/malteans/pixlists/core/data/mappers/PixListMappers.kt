package de.malteans.pixlists.core.data.mappers

import de.malteans.pixlists.core.data.database.entities.PixListEntity
import de.malteans.pixlists.core.data.serialization.JsonEntryDto
import de.malteans.pixlists.core.data.serialization.JsonListDto
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.core.domain.PixList
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

fun PixListEntity.toDomain(
    categories: List<PixCategory> = emptyList(),
    entries: Map<LocalDate, List<PixEntry>> = emptyMap()
): PixList {
    return PixList(
        id = this.id,
        name = this.name,
        categories = categories,
        entries = entries,
        years = Json.decodeFromString(this.years),
    )
}

fun PixList.toJsonDto(): JsonListDto {
    return JsonListDto(
        name = this.name,
        categories = this.categories.map { it.toJsonDto() },
        entries = this.entries.map { (date, entryList) ->
            JsonEntryDto(
                epochDays = date.toEpochDays(),
                categoryNames = entryList.map { it.category.name },
                categoryWeights = entryList.map { it.weight }
            )
        },
        years = this.years,
    )
}

fun JsonListDto.toEntity(id: Long = 0L): PixListEntity {
    return PixListEntity(
        id = id,
        name = this.name,
    )
}