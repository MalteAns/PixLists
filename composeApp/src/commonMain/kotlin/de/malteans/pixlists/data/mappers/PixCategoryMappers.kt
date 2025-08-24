package de.malteans.pixlists.data.mappers

import de.malteans.pixlists.data.database.entities.PixCategoryEntity
import de.malteans.pixlists.data.serialization.JsonCategoryDto
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.domain.PixColor

fun PixCategoryEntity.toDomain(color: PixColor?) : PixCategory {
    return PixCategory(
        id = id,
        listId = listId,
        color = color,
        name = name,
        orderIndex = orderIndex,
    )
}

fun PixCategory.toJsonDto(): JsonCategoryDto {
    return JsonCategoryDto(
        name = this.name,
        colorName = this.color?.name ?: "",
        orderIndex = this.orderIndex
    )
}

fun JsonCategoryDto.toEntity(id: Long = 0L, listId: Long, colorId: Long): PixCategoryEntity {
    return PixCategoryEntity(
        id = id,
        listId = listId,
        colorId = colorId,
        name = this.name,
        orderIndex = this.orderIndex
    )
}