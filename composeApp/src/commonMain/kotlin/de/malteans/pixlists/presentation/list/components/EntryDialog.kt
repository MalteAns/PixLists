package de.malteans.pixlists.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.presentation.components.CustomDialog
import de.malteans.pixlists.presentation.components.Dropdown
import de.malteans.pixlists.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.presentation.components.customIcons.OutlinedPixIcon
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.category
import pixlists.composeapp.generated.resources.set_entry
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun EntryDialog(
    categories: List<PixCategory>,
    onDismiss: () -> Unit,
    onSubmit: (LocalDate, List<PixCategory>) -> Unit,
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onDateChanged: (LocalDate) -> List<PixCategory>,
    curCategories: List<PixCategory> = emptyList(),
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    var selectedDate by remember { mutableStateOf(startDate) }
    var selectedCategories by remember { mutableStateOf(curCategories + null) }

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

    LaunchedEffect(selectedDate) {
        focusManager.clearFocus()
        selectedCategories = onDateChanged(selectedDate) + null
    }

    LaunchedEffect(selectedCategories) {
        if (selectedCategories.getOrElse(selectedCategories.lastIndex) { true } != null) {
            selectedCategories = selectedCategories + null
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
            IconButton(
                onClick = {
                    onSubmit(selectedDate, selectedCategories.filter { it != null } as List<PixCategory>)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    // FIXME: Get new data
                    selectedDate = selectedDate.minus(1, DateTimeUnit.DAY)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Previous Date",
                )
            }
            Text(
                text = selectedDate.asString(),
                modifier = Modifier
                    .clickable { showDatePickerDialog = true }
            )
            IconButton(
                onClick = {
                    // FIXME: Get new data
                    selectedDate = selectedDate.plus(1, DateTimeUnit.DAY)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = "Next Date",
                )
            }
        }
        LazyColumn(
            state = listState
        ) {
            items(
                count = selectedCategories.size,
            ) { index ->
                val selectedCategory = selectedCategories.getOrNull(index)
                CategoryListItem(
                    selectedCategory = selectedCategory,
                    options = categories.filter { it.id !in selectedCategories.map { it?.id } }.associateWith { it.name },
                    changeCategory = { newCategory ->
                        selectedCategories = selectedCategories.toMutableList().also {
                            it[index] = newCategory
                        }
                    },
                    removeCategory = {
                        selectedCategories = selectedCategories.toMutableList().also {
                            it.removeAt(index)
                        }
                    },
                    index = index + 1
                )
            }
        }
    }
}

@Composable
fun CategoryListItem(
    selectedCategory: PixCategory?,
    options: Map<PixCategory, String>,
    changeCategory: (PixCategory) -> Unit,
    removeCategory: () -> Unit,
    index: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Dropdown(
            modifier = Modifier.weight(1f),
            options = options as Map<Any, String>,
            label = "${stringResource(Res.string.category)} $index",
            onValueChanged = { changeCategory(it as PixCategory) },
            selectedOption = Pair(selectedCategory, selectedCategory?.name ?: ""),
            optionIcon = { category ->
                if (category != null) {
                    category as PixCategory
                    if (category.color != null) {
                        Icon(
                            imageVector = FilledPixIcon,
                            contentDescription = "Filled Pix",
                            tint = category.color.toColor(),
                        )
                    } else {
                        Icon(
                            imageVector = OutlinedPixIcon,
                            contentDescription = "Outlined Pix",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        )
        IconButton(
            onClick = {
                removeCategory()
            },
            enabled = selectedCategory != null,
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Delete Category",
                tint = if (selectedCategory == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun LocalDate.asString(): String {
    return "${dayOfWeek.toShortUiText().asString()}, " +
            "${day.toString().padStart(2, '0')}.${month.number.toString().padStart(2, '0')}.${this.year}"
}