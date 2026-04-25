package de.malteans.pixlists.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.Dropdown
import de.malteans.pixlists.core.presentation.components.FadeForScrollList
import de.malteans.pixlists.core.presentation.util.AnimatedDoubleIconButton
import de.malteans.pixlists.dashboard.domain.WidgetData
import de.malteans.pixlists.dashboard.domain.WidgetType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun WidgetDialog(
    pixLists: List<PixList>,
    widgetToEdit: WidgetData? = null,
    onDismissRequest: () -> Unit,
    onSubmit: (
        pixListId: Long,
        type: WidgetType,
        categoryIds: List<Long>,
    ) -> Unit,
    onDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var selectedPixList by remember { mutableStateOf(widgetToEdit?.pixList) }
    var selectedType by remember { mutableStateOf(widgetToEdit?.type) }
    @Suppress("RemoveExplicitTypeArguments") // It is in fact needed.
    var selectedCategoryIds by remember { mutableStateOf<List<Long>>(widgetToEdit?.categories?.map { it.id } ?: emptyList()) }

    val categories = pixLists.find { it.id == selectedPixList?.id }?.categories ?: emptyList()
    val isValidToAdd by remember { derivedStateOf {
        selectedPixList != null && selectedType != null && selectedCategoryIds.isNotEmpty()
    } }

    val categoryListScrollState = rememberScrollState()

    CustomDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(if (widgetToEdit == null) Res.string.add_widget else Res.string.edit_widget)) },
        rightIcons = {
            val showDelete by remember { derivedStateOf {
                widgetToEdit != null && widgetToEdit.pixList.id == selectedPixList?.id
                && widgetToEdit.type == selectedType && widgetToEdit.categories.map { it.id } == selectedCategoryIds
            } }
            var deleteClicked by remember { mutableStateOf(false) }
            LaunchedEffect(deleteClicked) {
                if (deleteClicked) {
                    delay(2.seconds)
                    deleteClicked = false
                }
            }
            AnimatedDoubleIconButton(
                showSecondary = showDelete,
                enabled = showDelete || isValidToAdd,
                onClick = {
                    if (showDelete) {
                        if (deleteClicked) onDelete()
                        deleteClicked = !deleteClicked
                    } else onSubmit(
                        selectedPixList!!.id, selectedType!!, selectedCategoryIds
                    )
                },
                primaryIcon = {
                    Icon(
                        imageVector = if (widgetToEdit == null) Icons.Outlined.AddBox else Icons.Outlined.Check,
                        contentDescription = stringResource(if (widgetToEdit == null) Res.string.add_widget else Res.string.edit_widget),
                    )
                },
                secondaryIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (deleteClicked) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // PixList selection dropdown
            Text(stringResource(Res.string.select_pixlist), style = MaterialTheme.typography.titleMedium)
            Dropdown(
                selectedOption = Pair(selectedPixList, selectedPixList?.name ?: ""),
                options = pixLists.associateWith { it.name },
                onValueChanged = {
                    selectedPixList = it
                    selectedCategoryIds = emptyList()
                    scope.launch { categoryListScrollState.scrollTo(0) }
                },
                initialExpanded = widgetToEdit == null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Widget Type selection
            Text(stringResource(Res.string.widget_type), style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow {
                val mainTypeOptions = listOf(
                    WidgetType.QUICK_ENTRY, WidgetType.STATISTICS
                )
                mainTypeOptions.forEach { widgetType ->
                    SegmentedButton(
                        selected = selectedType == widgetType || selectedType?.parentType == widgetType,
                        onClick = { selectedType = widgetType },
                        icon = {},
                        shape = SegmentedButtonDefaults.itemShape(
                            index = mainTypeOptions.indexOf(widgetType),
                            count = mainTypeOptions.size
                        )
                    ) {
                        Text(stringResource(widgetType.stringRes))
                    }
                }
            }
            // Statistic display type selection
            AnimatedVisibility(
                visible = selectedType == WidgetType.STATISTICS || selectedType?.parentType == WidgetType.STATISTICS,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                val options = mapOf(
                    WidgetType.STATISTICS to stringResource(Res.string.text),
//                    WidgetType.STATISTICS_PIE to stringResource(Res.string.pie_chart),
                    WidgetType.STATISTICS_COLUMNS to stringResource(Res.string.column_chart),
                    WidgetType.STATISTICS_LINES to stringResource(Res.string.line_chart),
                )
                Dropdown(
                    label = stringResource(Res.string.display_type),
                    selectedOption = Pair(selectedType, options[selectedType] ?: ""),
                    options = options,
                    onValueChanged = { selectedType = it },
                )
            }
            Spacer(Modifier.height(16.dp))

            // Category selection
            Text(
                text = stringResource(Res.string.categories_select),
                style = MaterialTheme.typography.titleMedium
            )
            if (categories.isEmpty()) {
                Text(
                    text = if (selectedPixList != null) stringResource(Res.string.no_categories_available)
                        else stringResource(Res.string.select_pixlist_first),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                FadeForScrollList(
                    showTopFade = categoryListScrollState.canScrollBackward,
                    showBottomFade = categoryListScrollState.canScrollForward,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(categoryListScrollState)
                    ) {
                        categories.forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedCategoryIds.contains(category.id),
                                        onClick = {
                                            selectedCategoryIds = if (selectedCategoryIds.contains(category.id)) {
                                                selectedCategoryIds - category.id
                                            } else {
                                                selectedCategoryIds + category.id
                                            }
                                        },
                                        role = Role.Checkbox
                                    )
                                    .padding(vertical = 8.dp)
                            ) {
                                val color = category.color?.toColor() ?: MaterialTheme.colorScheme.error
                                Checkbox(
                                    checked = selectedCategoryIds.contains(category.id),
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        uncheckedColor = color,
                                        checkedColor = color,
                                    )
                                )
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}