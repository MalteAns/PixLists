package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.malteans.pixlists.core.domain.Months
import de.malteans.pixlists.core.domain.PixEntry
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PixGrid(
    entries: Map<LocalDate, List<PixEntry>>,
    maxWeights: Map<Long, Int>,
    year: Int,
    enabled: Boolean,
    onEntryEdit: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val leapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    Column (
        modifier = modifier
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (monthNumber in 0..12) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f / 13f)
                ) {
                    when (monthNumber) {
                        0 -> {
                            for (j in 0..31) {
                                Row (
                                    modifier = Modifier
                                        .weight(1f / 32f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (j != 0) {
                                        Text (
                                            text = "$j".padStart(2, '0'),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            val month = Months.getByIndex(monthNumber)
                            for (day in 0..month.getDaysCount(leapYear)) {
                                Row (
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier
                                        .weight(1f / 32f)
                                ) {
                                    if (day == 0) {
                                        Text(
                                            text = month.getShortStringId,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    } else {
                                        val date = LocalDate(year, monthNumber, day)
                                        val dayEntries = entries.getOrElse(date) { emptyList() }

                                        PixCellCanvas(
                                            entries = dayEntries,
                                            maxWeights = maxWeights,
                                            isToday = date == today,
                                            enabled = enabled,
                                            onClick = { onEntryEdit(date) },
                                        )
                                    }
                                }
                            }
                            repeat (31 - month.getDaysCount(leapYear)) {
                                Spacer(Modifier.weight(1f / 32f))
                            }
                        }
                    }
                }
            }
        }
    }
}