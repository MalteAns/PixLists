package de.malteans.pixlists.lists.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.FadeForScrollList
import de.malteans.pixlists.core.presentation.util.ColumnChart
import de.malteans.pixlists.lists.presentation.view.components.PixCellCanvas
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.filter_categories
import pixlists.composeapp.generated.resources.list_statistics

@Composable
fun ListStatsScreenRoot(
    listId: Long,
    navigateBack: () -> Unit,
    viewModel: ListStatsViewModel = koinViewModel { parametersOf(listId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListStatsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ListStatsAction.NavigateBack -> navigateBack()
                else -> viewModel.onAction(action)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStatsScreen(
    state: ListStatsState,
    onAction: (ListStatsAction) -> Unit,
) {
    var showFilterDialog by remember { mutableStateOf(true) }
    if (showFilterDialog) {
        CustomDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text(stringResource(Res.string.filter_categories)) },
            leftIcons = {
                val state by remember(state.selectedCategories, state.allCategories) { derivedStateOf {
                    when {
                        state.selectedCategories.isEmpty() -> ToggleableState.Off
                        state.selectedCategories.size == state.allCategories.size -> ToggleableState.On
                        else -> ToggleableState.Indeterminate
                    }
                } }
                TriStateCheckbox(
                    state = state,
                    onClick = {
                        onAction(ListStatsAction.SetAllCategoriesSelected(
                            selected = state != ToggleableState.On
                        ))
                    }
                )
            },
            rightIcons = {
                IconButton(onClick = { showFilterDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Apply filter",
                    )
                }
            }
        ) {
            val listState = rememberLazyListState()

            FadeForScrollList(
                showTopFade = listState.canScrollBackward,
                showBottomFade = listState.canScrollForward,
            ){
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .heightIn(max = 600.dp)
                ) {
                    items(state.allCategories) { category ->
                        val selected = state.selectedCategories.contains(category)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected,
                                    onClick = {
                                        onAction(ListStatsAction.SetCategorySelected(category.id, !selected))
                                    },
                                    role = Role.Checkbox,
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = null,
                                colors = category.color?.toColor()?.let { categoryColor ->
                                    CheckboxDefaults.colors(
                                        checkedColor = categoryColor,
                                        uncheckedColor = categoryColor,
                                    )
                                } ?: CheckboxDefaults.colors()
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = category.name)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.list_statistics)) },
                navigationIcon = {
                    IconButton({ onAction(ListStatsAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter categories"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showFilterDialog) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    ColumnChart(
                        dataMap = state.absoluteMap.mapValues { it.value.toDouble() },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(state.selectedCategories) { category ->
                val relCount = state.relativeMap[category]
                val absCount = state.absoluteMap[category]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = spacedBy(8.dp),
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                ) {
                    PixCellCanvas(
                        categories = listOf(category),
                        animation = false
                    )
                    Text(
                        text = category.name,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "$absCount (${(relCount?.times(100))?.fastRoundToInt()}%)")
                }
            }
        }
    }
}