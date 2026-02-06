@file:OptIn(ExperimentalTime::class)

package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.cancel
import pixlists.composeapp.generated.resources.select_date
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (LocalDate) -> Unit,
    initialSelectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toEpochDays() * 24L * 60L * 60L * 1000L,
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onSubmit(datePickerState.selectedDateMillis!!.toLocalDate()) },
            ) {
                Text(
                    text = stringResource(Res.string.select_date),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(Res.string.cancel),
                )
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.UTC): LocalDate {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone).date
}