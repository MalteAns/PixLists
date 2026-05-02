package de.malteans.pixlists.dashboard.domain

import org.jetbrains.compose.resources.StringResource
import pixlists.composeapp.generated.resources.*

enum class WidgetType(
    val stringRes: StringResource,
    val parentType: WidgetType? = null,
) {
    QUICK_ENTRY(Res.string.quick_entry),
    STATISTICS(Res.string.statistics),
    STATISTICS_PIE(Res.string.pie_chart, parentType = STATISTICS),
    STATISTICS_COLUMNS(Res.string.column_chart, parentType = STATISTICS),
//    STATISTICS_BARS(Res.string.bar_chart, parentType = STATISTICS),
    STATISTICS_LINES(Res.string.line_chart, parentType = STATISTICS),

    ;
    
    val headingRes: StringResource
        get() = when (this) {
            QUICK_ENTRY -> Res.string.quick_entry_heading
            STATISTICS,
            STATISTICS_PIE,
            STATISTICS_COLUMNS,
            STATISTICS_LINES -> Res.string.statistics_heading
        }
}