package de.malteans.pixlists.dashboard.presentation

import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.dashboard.domain.WidgetData

data class DashboardState(
    val pixLists: List<PixList> = emptyList(),
    val widgets: List<WidgetData> = emptyList(),
)
