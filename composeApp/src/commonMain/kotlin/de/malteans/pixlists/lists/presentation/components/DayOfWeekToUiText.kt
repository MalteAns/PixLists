package de.malteans.pixlists.lists.presentation.components

import de.malteans.pixlists.core.presentation.components.UiText
import kotlinx.datetime.DayOfWeek
import pixlists.composeapp.generated.resources.*

fun DayOfWeek.toUiText(): UiText {
    return UiText.FromStringResource(when (this) {
        DayOfWeek.MONDAY -> Res.string.monday
        DayOfWeek.TUESDAY -> Res.string.tuesday
        DayOfWeek.WEDNESDAY -> Res.string.wednesday
        DayOfWeek.THURSDAY -> Res.string.thursday
        DayOfWeek.FRIDAY -> Res.string.friday
        DayOfWeek.SATURDAY -> Res.string.saturday
        DayOfWeek.SUNDAY -> Res.string.sunday
    })
}

fun DayOfWeek.toShortUiText(): UiText {
    return UiText.FromStringResource(when (this) {
        DayOfWeek.MONDAY -> Res.string.short_monday
        DayOfWeek.TUESDAY -> Res.string.short_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.short_wednesday
        DayOfWeek.THURSDAY -> Res.string.short_thursday
        DayOfWeek.FRIDAY -> Res.string.short_friday
        DayOfWeek.SATURDAY -> Res.string.short_saturday
        DayOfWeek.SUNDAY -> Res.string.short_sunday
    })
}