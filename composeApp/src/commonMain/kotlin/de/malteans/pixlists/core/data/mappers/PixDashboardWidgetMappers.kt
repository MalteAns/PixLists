package de.malteans.pixlists.core.data.mappers

import de.malteans.pixlists.core.data.database.entities.PixDashboardWidgetEntity
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.dashboard.domain.PixDashboardWidget

fun PixDashboardWidgetEntity.toDomain(pixList: PixList, categoryIds: List<Long>) = PixDashboardWidget(
    id = id,
    pixList = pixList,
    type = type,
    categoryIds = categoryIds,
)

fun PixDashboardWidget.toEntity() = PixDashboardWidgetEntity(
    id = id,
    listId = pixList.id,
    type = type,
)