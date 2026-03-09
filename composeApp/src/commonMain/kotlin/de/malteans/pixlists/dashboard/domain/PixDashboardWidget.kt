package de.malteans.pixlists.dashboard.domain

import de.malteans.pixlists.core.domain.PixList

data class PixDashboardWidget(
    val id: Long,
    val pixList: PixList,
    val type: WidgetType,
    val categoryIds: List<Long>,
)
