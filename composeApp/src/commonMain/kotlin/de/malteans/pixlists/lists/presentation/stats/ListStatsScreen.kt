package de.malteans.pixlists.lists.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.core.presentation.components.CustomDialog
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
    var showFilterDialog by remember { mutableStateOf(false) }
    if (showFilterDialog) {
        CustomDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text(stringResource(Res.string.filter_categories)) },
            rightIcons = {
                IconButton(onClick = { showFilterDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Apply filter",
                    )
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 600.dp)
            ) {
                items(state.allCategories) { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = state.statsMap.keys.contains(category),
                            onCheckedChange = { isChecked ->
                                onAction(ListStatsAction.IncludeCategory(category, isChecked))
                            },
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
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            items(state.allCategories.map { category -> category to state.statsMap[category] }.filter { it.second != null }) { (category, stats) ->
                val (absCount, relCount) = stats!!
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
                    )
                    Text(
                        text = category.name,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "$absCount (${(relCount * 100).fastRoundToInt()}%)")
                }
            }
        }
    }
}