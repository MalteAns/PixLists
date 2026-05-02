package de.malteans.pixlists.dashboard.domain

import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixList

data class WidgetData(
    val id: Long,
    val pixList: PixList,
    val type: WidgetType,
    val categories: List<PixCategory>,
    val orderIndex: Int = 0,
)
