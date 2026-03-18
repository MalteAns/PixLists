package de.malteans.pixlists.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.core.presentation.components.CustomTopBar
import de.malteans.pixlists.core.presentation.util.ObserveAsEvents
import de.malteans.pixlists.dashboard.domain.WidgetData
import de.malteans.pixlists.dashboard.domain.WidgetType
import de.malteans.pixlists.dashboard.presentation.components.QuickEntryWidget
import de.malteans.pixlists.dashboard.presentation.components.StatisticWidget
import de.malteans.pixlists.dashboard.presentation.components.WidgetDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.add_widget
import pixlists.composeapp.generated.resources.app_name
import pixlists.composeapp.generated.resources.edit_widget

@Composable
fun DashboardScreenRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    openDrawer: () -> Unit,
    openList: (listId: Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is DashboardEvent.OnOpenList -> openList(event.listId)
        }
    }

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.OpenDrawer -> openDrawer()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    var addWidgetDialog by remember { mutableStateOf(false) }
    var widgetToEdit by remember { mutableStateOf<WidgetData?>(null) }

    if (addWidgetDialog || widgetToEdit != null) {
        WidgetDialog(
            pixLists = state.pixLists,
            widgetToEdit = widgetToEdit,
            onDismissRequest = {
                addWidgetDialog = false
                widgetToEdit = null
            },
            onSubmit = { pixListId, type, categoryIds ->
                if (widgetToEdit == null) {
                    onAction(DashboardAction.AddWidget(pixListId, type, categoryIds))
                } else {
                    onAction(DashboardAction.EditWidget(widgetToEdit!!.id, pixListId, type, categoryIds))
                }
                addWidgetDialog = false
                widgetToEdit = null
            },
            onDelete = {
                onAction(DashboardAction.DeleteWidget(widgetToEdit!!.id))
                widgetToEdit = null
            },
        )
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.app_name),
                openDrawer = { onAction(DashboardAction.OpenDrawer) },
                actions = {
                    IconButton({ addWidgetDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.AddBox,
                            contentDescription = stringResource(Res.string.add_widget),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            verticalArrangement = spacedBy(16.dp),
            horizontalArrangement = spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
        ) {
            items(state.widgets, key = { it.id }) { widget ->
                val index = state.widgets.indexOf(widget)
                Column {
                    if (index == 0) Spacer(Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(widget.type.headingRes, widget.pixList.name),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { widgetToEdit = widget },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = stringResource(Res.string.edit_widget)
                                    )
                                }
                            }
                            when {
                                WidgetType.QUICK_ENTRY == widget.type -> QuickEntryWidget(
                                    categories = widget.categories,
                                    onAddEntry = { category ->
                                        onAction(DashboardAction.AddTodayEntry(widget.pixList.id, category.id))
                                    },
                                )
                                WidgetType.STATISTICS == widget.type || WidgetType.STATISTICS == widget.type.parentType -> {
                                    StatisticWidget(
                                        type = widget.type,
                                        pixList = widget.pixList,
                                        categories = widget.categories,
                                    )
                                }
                            }
                        }
                    }
                    if (index == state.widgets.lastIndex) Spacer(Modifier.height(64.dp))
                }
            }
        }
    }
}