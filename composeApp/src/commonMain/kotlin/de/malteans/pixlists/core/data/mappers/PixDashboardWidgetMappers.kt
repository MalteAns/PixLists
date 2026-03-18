package de.malteans.pixlists.core.data.mappers

import de.malteans.pixlists.core.data.database.entities.PixDashboardWidgetEntity
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.dashboard.domain.WidgetData

fun PixDashboardWidgetEntity.toDomain(pixList: PixList, categories: List<PixCategory>) = WidgetData(
    id = id,
    pixList = pixList,
    type = type,
    categories = categories,
)

fun WidgetData.toEntity() = PixDashboardWidgetEntity(
    id = id,
    listId = pixList.id,
    type = type,
)