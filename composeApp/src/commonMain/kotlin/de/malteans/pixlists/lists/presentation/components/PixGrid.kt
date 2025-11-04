package de.malteans.pixlists.lists.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.malteans.pixlists.core.domain.Months
import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PixGrid(
    entries: Map<LocalDate, List<PixCategory>>,
    year: Int,
    enabled: Boolean,
    onEntryEdit: (LocalDate, List<PixCategory>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

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
                                    val text = if (j == 0) {
                                        null
                                    } else if (j < 10) {
                                        "0$j"
                                    } else {
                                        "$j"
                                    }
                                    if (text != null) {
                                        Text (
                                            text = text,
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
                            for (day in 0..month.getDaysCount) {
                                Row (
                                    modifier = Modifier
                                        .weight(1f / 32f),
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    if (day == 0) {
                                        Text(
                                            text = month.getShortStringId,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    } else {
                                        val date = LocalDate(year, monthNumber, day)
                                        val pixCategories = entries.getOrElse(date) { emptyList() }

                                        PixCellCanvas(
                                            categories = pixCategories,
                                            isToday = date == today,
                                            enabled = enabled,
                                            onClick = { onEntryEdit(date, pixCategories) },
                                        )

                                        return@Row
                                    }
                                }
                            }
                            for (r in 0 until 31 - month.getDaysCount) {
                                Row (
                                    modifier = Modifier
                                        .weight(1f / 32f),
                                ) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}