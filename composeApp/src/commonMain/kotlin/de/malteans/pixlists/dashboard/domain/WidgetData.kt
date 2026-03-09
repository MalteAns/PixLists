package de.malteans.pixlists.dashboard.domain

open class WidgetData(val type: WidgetType) {
    data class QuickEntry(
        val listId: Long,
        val preSelectedCategories: List<Long>
    ): WidgetData(WidgetType.QUICK_ENTRY)
}