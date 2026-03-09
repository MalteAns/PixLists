package de.malteans.pixlists.dashboard.presentation

import de.malteans.pixlists.dashboard.domain.PixDashboardWidget

data class DashboardState(
    val widgets: List<PixDashboardWidget> = emptyList(),
)
