package de.malteans.pixlists.presentation.list.components

import de.malteans.pixlists.presentation.components.UiText
import kotlinx.datetime.DayOfWeek
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.friday
import pixlists.composeapp.generated.resources.monday
import pixlists.composeapp.generated.resources.saturday
import pixlists.composeapp.generated.resources.short_friday
import pixlists.composeapp.generated.resources.short_monday
import pixlists.composeapp.generated.resources.short_saturday
import pixlists.composeapp.generated.resources.short_sunday
import pixlists.composeapp.generated.resources.short_thursday
import pixlists.composeapp.generated.resources.short_tuesday
import pixlists.composeapp.generated.resources.short_wednesday
import pixlists.composeapp.generated.resources.sunday
import pixlists.composeapp.generated.resources.thursday
import pixlists.composeapp.generated.resources.tuesday
import pixlists.composeapp.generated.resources.wednesday

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