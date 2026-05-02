package de.malteans.pixlists.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
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
import pixlists.composeapp.generated.resources.*
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
    var reorderMode by remember { mutableStateOf(false) }
    var widgetToEdit by remember { mutableStateOf<WidgetData?>(null) }
    var scrollAfterReorder by remember { mutableStateOf(false) }

    var reorderedWidgets by remember(state.widgets) { mutableStateOf(state.widgets) }
    val hapticFeedback = LocalHapticFeedback.current
    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(state.widgets) {
        if (scrollAfterReorder && !reorderMode) {
            lazyGridState.scrollToItem(0)
            scrollAfterReorder = false
        }
    }

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
                    if (reorderMode) {
                        IconButton({
                            onAction(DashboardAction.UpdateWidgetOrder(reorderedWidgets.map { it.id }))
                            reorderMode = false
                            scrollAfterReorder = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(Res.string.done)
                            )
                        }
                    } else {
                        if (state.widgets.isNotEmpty()) {
                            IconButton({ reorderMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(Res.string.reorder_widgets),
                                )
                            }
                        }
                        IconButton({ addWidgetDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.AddBox,
                                contentDescription = stringResource(Res.string.add_widget),
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (reorderMode) {
            val lazyListState = rememberLazyListState()
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                reorderedWidgets = reorderedWidgets.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(reorderedWidgets, key = { it.id }) { widget ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = widget.id
                    ) { isDragging ->
                        val elevation = if (isDragging) 8.dp else 0.dp
                        Surface(
                            shadowElevation = elevation,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        onAction(DashboardAction.DeleteWidget(widget.id))
                                        reorderedWidgets = reorderedWidgets.filter { it.id != widget.id }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(Res.string.delete),
                                        tint = MaterialTheme.colorScheme.errorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(widget.type.headingRes, widget.pixList.name),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.DragHandle,
                                    contentDescription = stringResource(Res.string.drag_to_reorder),
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Adaptive(300.dp),
                verticalArrangement = spacedBy(16.dp),
                horizontalArrangement = spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding)
            ) {
                if (state.widgets.isEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(top = 64.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.dashboard_no_widgets_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    return@LazyVerticalGrid
                }
                itemsIndexed(state.widgets, key = { _, widget -> widget.id }) { index, widget ->
                    Column {
                        if (index < lazyGridState.layoutInfo.maxSpan) Spacer(Modifier.height(16.dp))
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
}