package de.malteans.pixlists.core.data.mappers

import de.malteans.pixlists.core.data.database.entities.PixCategoryEntity
import de.malteans.pixlists.core.data.serialization.JsonCategoryDto
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor

fun PixCategoryEntity.toDomain(color: PixColor?)
    = PixCategory(
        id = id,
        listId = listId,
        color = color,
        name = name,
        orderIndex = orderIndex,
        enableWeight = enableWeight,
        minWeight = minWeight,
        maxWeight = maxWeight,
        weightStep = weightStep,
    )


fun PixCategory.toEntity()
    = PixCategoryEntity(
        id = id,
        listId = listId,
        colorId = color?.id,
        name = name,
        orderIndex = orderIndex,
        enableWeight = enableWeight,
        minWeight = minWeight,
        maxWeight = maxWeight,
        weightStep = weightStep,
    )

fun PixCategory.toJsonDto()
    = JsonCategoryDto(
        name = this.name,
        colorName = this.color?.name ?: "",
        orderIndex = this.orderIndex,
        enableWeight = this.enableWeight,
        minWeight = this.minWeight,
        maxWeight = this.maxWeight,
        weightStep = this.weightStep,
    )

fun JsonCategoryDto.toEntity(id: Long = 0L, listId: Long, colorId: Long)
    = PixCategoryEntity(
        id = id,
        listId = listId,
        colorId = colorId,
        name = this.name,
        orderIndex = this.orderIndex,
        enableWeight = this.enableWeight ?: PixCategory.DEFAULT_ENABLE_WEIGHT,
        minWeight = this.minWeight ?: PixCategory.DEFAULT_MIN_WEIGHT,
        maxWeight = this.maxWeight ?: PixCategory.DEFAULT_MAX_WEIGHT,
        weightStep = this.weightStep ?: PixCategory.DEFAULT_WEIGHT_STEP,
    )
