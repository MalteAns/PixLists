package de.malteans.pixlists.core.data.mappers

import de.malteans.pixlists.core.data.database.entities.PixColorEntity
import de.malteans.pixlists.core.data.serialization.JsonColorDto
import de.malteans.pixlists.core.domain.PixColor

fun PixColorEntity.toDomain(): PixColor {
    return PixColor(
        id = id,
        name = name,
        red = red,
        green = green,
        blue = blue,
    )
}

fun PixColorEntity.toJsonDto(): JsonColorDto {
    return JsonColorDto(
        name = this.name,
        red = this.red,
        green = this.green,
        blue = this.blue,
    )
}

fun JsonColorDto.toEntity(id: Long = 0L): PixColorEntity {
    return PixColorEntity(
        id = id,
        name = this.name,
        red = this.red,
        green = this.green,
        blue = this.blue,
    )
}