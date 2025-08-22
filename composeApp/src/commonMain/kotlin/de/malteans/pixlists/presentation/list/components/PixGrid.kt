package de.malteans.pixlists.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.malteans.pixlists.domain.Months
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.domain.PixList
import de.malteans.pixlists.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.presentation.components.customIcons.OutlinedPixIcon
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PixGrid(
    curPixList: PixList,
    enabled: Boolean,
    onEntryEdit: (LocalDate, PixCategory?) -> Unit,
    modifier: Modifier,
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
                                        val date = LocalDate(2025, monthNumber, day)
                                        curPixList.entries[date]
                                            .let { pixCategory ->
                                                IconButton(
                                                    onClick = {
                                                        if (enabled) {
                                                            onEntryEdit(
                                                                date,
                                                                pixCategory
                                                            )
                                                        }
                                                    },
                                                ) {
                                                    Box {
                                                        if (pixCategory == null) {
                                                            Icon(
                                                                imageVector = OutlinedPixIcon,
                                                                contentDescription = "Empty Pix",
                                                                tint = if (date == today) {
                                                                    MaterialTheme.colorScheme.primary
                                                                } else {
                                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                                        alpha = 0.5f
                                                                    )
                                                                }
                                                            )
                                                        } else {
                                                            if (pixCategory.color != null) {
                                                                Icon(
                                                                    imageVector = FilledPixIcon,
                                                                    contentDescription = "Pix",
                                                                    tint = pixCategory.color.toColor(),
                                                                )
                                                                if (date == today) {
                                                                    Icon(
                                                                        imageVector = OutlinedPixIcon,
                                                                        contentDescription = "Today Pix",
                                                                        tint = MaterialTheme.colorScheme.primary
                                                                    )
                                                                }
                                                            } else {
                                                                Icon(
                                                                    imageVector = OutlinedPixIcon,
                                                                    contentDescription = "Empty Pix",
                                                                    tint = MaterialTheme.colorScheme.error
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
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