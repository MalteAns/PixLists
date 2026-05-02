package de.malteans.pixlists.dashboard.presentation

sealed interface DashboardEvent {
    data class OnOpenList(val listId: Long) : DashboardEvent
}