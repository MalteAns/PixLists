package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.IconIconButton
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun EntryDialog(
    categories: List<PixCategory>,
    maxWeights: Map<Long, Int>, // CategoryId to maxWeight
    entries: Map<LocalDate, List<PixEntry>>,
    onDismiss: () -> Unit,
    onSubmit: (changes: Map<LocalDate, List<PixEntry>>) -> Unit,
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    val focusManager = LocalFocusManager.current

    var changesMap by remember { mutableStateOf(emptyMap<LocalDate, List<PixEntry>>()) }

    var selectedDate by remember { mutableStateOf(startDate) }
    var selectedEntries: List<PixEntry?> by remember(changesMap, selectedDate) {
        mutableStateOf((changesMap[selectedDate] ?: entries[selectedDate] ?: emptyList()) + null)
    }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    if (showDatePickerDialog) {
        CustomDatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            onSubmit = { newDate ->
                selectedDate = newDate
                showDatePickerDialog = false
            },
            initialSelectedDate = selectedDate
        )
    }

    var showSubmitAllDialog by remember { mutableStateOf(false) }
    if (showSubmitAllDialog) {
        CustomDialog(
            onDismissRequest = { showSubmitAllDialog = false },
            title = { Text(stringResource(Res.string.submit_changes_title)) },
            leftIcons = {
                IconIconButton(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    onClick = { showSubmitAllDialog = false },
                )
            }
        ) {
            Text(stringResource(Res.string.submit_changes_desc, selectedDate.asString()))
            Row(
                horizontalArrangement = spacedBy(8.dp, Alignment.End),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button({
                    onSubmit(changesMap[selectedDate]?.let { dateEntries ->
                        mapOf(selectedDate to dateEntries)
                    } ?: emptyMap())
                    showSubmitAllDialog = false
                }) {
                    Text(stringResource(Res.string.selected_date))
                }
                Button({
                    onSubmit(changesMap.filter { entries[it.key] != it.value }) // filter "fake" changes
                    showSubmitAllDialog = false
                }) {
                    Text(stringResource(Res.string.all))
                }
            }
        }
    }

    CustomDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.set_entry),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        leftIcons = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        rightIcons = {
            IconIconButton(
                imageVector = Icons.Default.Check,
                contentDescription = "Submit",
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    val filteredChanges = changesMap.filter {
                        entries[it.key] != it.value
                    }

                    if (filteredChanges.size == 1)
                        onSubmit(filteredChanges)
                    else {
                        showSubmitAllDialog = true
                    }
                },
            )
        },
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconIconButton(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Previous Date",
                onClick = {
                    selectedDate = selectedDate.minus(1, DateTimeUnit.DAY)
                    focusManager.clearFocus()
                }
            )
            Text(
                text = selectedDate.asString(),
                modifier = Modifier
                    .clickable {
                        showDatePickerDialog = true
                        focusManager.clearFocus()
                    }
            )
            IconIconButton(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = "Next Date",
                onClick = {
                    selectedDate = selectedDate.plus(1, DateTimeUnit.DAY)
                    focusManager.clearFocus()
                }
            )
        }
        LazyColumn {
            items(
                count = selectedEntries.size,
            ) { index ->
                val selectedEntry = selectedEntries.getOrNull(index)

                EntryDialogItem(
                    index = index + 1,
                    initialExpanded = index == 0 && selectedEntry == null,
                    selectedCategory = selectedEntry?.category,
                    options = categories
                        .filter { category -> category.id !in selectedEntries.map { it?.category?.id } }
                        .associateWith { it.name },
                    changeCategory = { newCategory ->
                        changesMap = changesMap.toMutableMap().also { map ->
                            val currentEntries = selectedEntries.toMutableList()
                            val weight = currentEntries.getOrNull(index)?.weight
                            currentEntries[index] = PixEntry(category = newCategory, weight = weight)
                            map[selectedDate] = currentEntries.filterNotNull()
                        }
                    },
                    weight = selectedEntry?.weight ?: selectedEntry?.category?.maxWeight,
                    maxWeights = maxWeights,
                    onWeightChange = { newWeight ->
                        if (selectedEntry == null) return@EntryDialogItem // User has to select category first
                        changesMap = changesMap.toMutableMap().also { map ->
                            val currentEntries = selectedEntries.toMutableList()
                            currentEntries[index] = selectedEntry.copy(weight = newWeight)
                            map[selectedDate] = currentEntries.filterNotNull()
                        }
                    },
                    removeEntry = {
                        changesMap = changesMap.toMutableMap().also { map ->
                            val currentEntries = selectedEntries.toMutableList()
                            currentEntries.removeAt(index)
                            map[selectedDate] = currentEntries.filterNotNull()
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun LocalDate.asString(): String {
    return "${dayOfWeek.toShortUiText().asString()}, " +
            "${day.toString().padStart(2, '0')}.${month.number.toString().padStart(2, '0')}.${this.year}"
}