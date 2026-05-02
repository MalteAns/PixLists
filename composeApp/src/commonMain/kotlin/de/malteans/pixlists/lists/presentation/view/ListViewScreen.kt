package de.malteans.pixlists.lists.presentation.view

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.presentation.components.CustomTopBar
import de.malteans.pixlists.lists.presentation.view.components.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.no_pixlist_selected
import pixlists.composeapp.generated.resources.open_statistics
import pixlists.composeapp.generated.resources.rename_list
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ListViewScreenRoot(
    curPixListId: Long?,
    openDrawer: () -> Unit,
    openStats: () -> Unit,
    viewModel: ListViewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(curPixListId) {
        viewModel.onAction(ListViewAction.SetPixListId(curPixListId))
    }

    ListViewScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ListViewAction.OpenDrawer -> openDrawer()
                is ListViewAction.OpenStats -> openStats()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun ListViewScreen(
    state: ListViewState,
    onAction: (ListViewAction) -> Unit,
) {
//    BackHandler {
//        viewModel.undoLastAction()
//    }

    val pagerState = rememberPagerState(
        initialPage = state.selectedYearIndex,
        pageCount = { state.possibleYears.size }
    )

    LaunchedEffect(state.selectedYearIndex) {
        pagerState.animateScrollToPage(state.selectedYearIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { yearIndex ->
            onAction(ListViewAction.OnYearSelected(yearIndex))
        }
    }

    LaunchedEffect(state.listStatus) {
        if (state.listStatus == ListStatus.OPENED) {
            val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            val yearIndex = state.possibleYears.indexOf(currentYear)
            if (yearIndex != -1) {
                onAction(ListViewAction.OnYearSelected(yearIndex))
            } else {
                onAction(ListViewAction.OnAddCurrentYear(currentYear))
            }
        }
    }

    var lastPossibleYears by remember { mutableStateOf(state.possibleYears) }
    LaunchedEffect(state.possibleYears.size) {
        if (lastPossibleYears.size == state.possibleYears.size - 1) {
            onAction(ListViewAction.OnYearSelected(
                yearIndex = state.possibleYears.indexOfFirst {
                    it !in lastPossibleYears
                }
            ))
        }
        lastPossibleYears = state.possibleYears
    }

    var showEntryDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<LocalDate?>(null) }

    if (showEntryDialog) {
        val startDate: LocalDate? = entryToEdit

        val onDismiss = {
            showEntryDialog = false
            entryToEdit = null
        }

        EntryDialog(
            categories = state.curCategories,
            entries = state.curPixList?.entries ?: emptyMap(),
            onDismiss = onDismiss,
            onSubmit = { changes ->
                changes.forEach { change ->
                    onAction(ListViewAction.SetPixEntry(change.key, change.value))
                }
                onDismiss()
            },
            startDate = startDate
                ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        )
    }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<PixCategory?>(null) }

    if (showCategoryDialog) {
        CategoryDialog(
            onDismiss = {
                showCategoryDialog = false
                categoryToEdit = null
            },
            onSubmit = { name, color, isEdit ->
                if (isEdit) {
                    onAction(
                        ListViewAction.UpdatePixCategory(
                            categoryToEdit!!,
                            name,
                            color
                        )
                    )
                } else {
                    onAction(
                        ListViewAction.CreatePixCategory(
                            name!!,
                            color!!
                        )
                    )
                }
                showCategoryDialog = false
                categoryToEdit = null
            },
            onDelete = {
                onAction(ListViewAction.DeletePixCategory(categoryToEdit!!))
                showCategoryDialog = false
                categoryToEdit = null
            },
            colors = state.colorList,
            invalidNames = state.curCategories.map { it.name },
            isEdit = categoryToEdit != null,
            categoryToEdit = categoryToEdit,
        )
    }

    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        RenamePixListDialog(
            curName = state.curPixList?.name ?: "",
            invalideNames = state.invalideNames,
            onDismiss = { showRenameDialog = false },
            onFinish = { newName ->
                onAction(ListViewAction.UpdatePixListName(newName))
                showRenameDialog = false
            }
        )
    }

    Scaffold (
        topBar = {
            CustomTopBar(
                title = {
                    AnimatedVisibility(
                        visible = state.curPixList != null,
                        enter = scaleIn(tween(easing = EaseOutBack)),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(state.curPixList?.name ?: "")
                            Row {
                                val localContentColor = LocalContentColor.current
                                state.possibleYears.forEachIndexed { index, year ->
                                    Text(
                                        text = year.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        color = localContentColor.copy(
                                            alpha = if (index == state.selectedYearIndex) 1f
                                                else 0.4f,
                                        ),
                                        modifier = Modifier
                                            .clickable { onAction(ListViewAction.OnYearSelected(index)) }
                                    )
                                    if (index < state.possibleYears.lastIndex) {
                                        Text(
                                            text = " | ",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.curPixList != null,
                        enter = scaleIn(tween(easing = EaseOutBack)),
                    ) {
                        var showDropdown by remember { mutableStateOf(false) }

                        IconButton(onClick = { showDropdown = !showDropdown }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Actions",
                            )
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(Res.string.rename_list),
                                    )
                                },
                                text = { Text(stringResource(Res.string.rename_list)) },
                                onClick = {
                                    showRenameDialog = true
                                    showDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.QueryStats,
                                        contentDescription = stringResource(Res.string.open_statistics),
                                    )
                                },
                                text = { Text(stringResource(Res.string.open_statistics)) },
                                onClick = {
                                    onAction(ListViewAction.OpenStats)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                },
                openDrawer = { onAction(ListViewAction.OpenDrawer) },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.curPixList != null  && state.curCategories.isNotEmpty(),
                enter = slideIn(tween(500, easing = EaseOutBack)) { IntOffset(it.width, it.height) },
                exit = slideOut(tween(500, easing = EaseOutBack)) { IntOffset(it.width, it.height) },
            ) {
                FloatingActionButton({ showEntryDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Entry",
                    )
                }
            }
        }
    ) { pad ->
        Box (
            modifier = Modifier
                .padding(pad)
                .padding(start = 8.dp, end = 4.dp, bottom = 16.dp)
        ) {
            if (state.listStatus != ListStatus.EMPTY) {
                Row {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(0.8f)
                    ) { page ->
                        state.possibleYears.getOrNull(page)?.let { year ->
                            PixGrid(
                                entries = state.curPixList?.entries ?: emptyMap(),
                                enabled = state.curCategories.isNotEmpty(),
                                year = year,
                                onEntryEdit = { date ->
                                    entryToEdit = date
                                    showEntryDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        } ?: run {
                            Box(Modifier.fillMaxSize()) {
                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                            }
                        }
                    }

                    // Categories -----------------------------------------------------------------
                    CategoryList(
                        curCategories = state.curCategories,
                        onEditCategory = { category ->
                            categoryToEdit = category
                            showCategoryDialog = true
                        },
                        onCreateCategory = {
                            categoryToEdit = null
                            showCategoryDialog = true
                        },
                        onUpdateOrder = {
                            onAction(ListViewAction.UpdatePixCategoryOrder(it))
                        },
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(bottom = 64.dp)
                    )
                }
                AnimatedVisibility(
                    visible = state.listStatus != ListStatus.OPENED,
                    enter = EnterTransition.None,
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { /* Consume tap to prevent clicks "through" this box */ }
                                )
                            }
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .fillMaxSize()
                    )
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(resource = Res.string.no_pixlist_selected),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}