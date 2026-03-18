package de.malteans.pixlists.dashboard.presentation

import de.malteans.pixlists.dashboard.domain.WidgetType

sealed interface DashboardAction {
    data object OpenDrawer : DashboardAction

    data class AddWidget(
        val pixListId: Long, val type: WidgetType, val categoryIds: List<Long>
    ) : DashboardAction
    data class EditWidget(
        val widgetId: Long, val pixListId: Long, val type: WidgetType, val categoryIds: List<Long>
    ) : DashboardAction
    data class DeleteWidget(val widgetId: Long) : DashboardAction

    data class AddTodayEntry(val pixListId: Long, val categoryId: Long) : DashboardAction

}