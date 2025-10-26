package de.malteans.pixlists.presentation.list

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.presentation.components.CustomTopBar
import de.malteans.pixlists.presentation.list.components.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.no_pixlist_selected
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ListScreenRoot(
    viewModel: ListViewModel = koinViewModel(),
    openDrawer: () -> Unit,
    curPixListId: Long?,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(curPixListId) {
        when (curPixListId) {
            null -> viewModel.onAction(ListAction.SetListStatus(ListStatus.EMPTY))
            else -> {
                viewModel.onAction(ListAction.SetListStatus(ListStatus.LOADING))
                viewModel.onAction(ListAction.SetPixListId(curPixListId))
            }
        }
    }

    LaunchedEffect(state.curPixList) {
        if (state.curPixList != null) {
            viewModel.onAction(ListAction.SetListStatus(ListStatus.OPENED))
        }
    }

    ListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ListAction.OpenDrawer-> openDrawer()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
fun ListScreen(
    state: ListState,
    onAction: (ListAction) -> Unit,
) {
//    BackHandler {
//        viewModel.undoLastAction()
//    }

    var showEntryDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<LocalDate?>(null) }
    var curEntryCategories by remember { mutableStateOf<List<PixCategory>>(emptyList()) }

    if (showEntryDialog) {
        val startDate: LocalDate? = entryToEdit
        val curCategories: List<PixCategory> = curEntryCategories

        val onDismiss = {
            showEntryDialog = false
            entryToEdit = null
            curEntryCategories = emptyList()
        }

        EntryDialog(
            categories = state.curCategories,
            onDismiss = onDismiss,
            onSubmit = { date, categories ->
                onAction(ListAction.SetPixEntry(date, categories))
                onDismiss()
            },
            startDate = startDate
                ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            onDateChanged = { newDate ->
                state.curPixList?.entries[newDate] ?: emptyList()
            },
            curCategories = curCategories
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
                    onAction(ListAction.UpdatePixCategory(
                        categoryToEdit!!,
                        name,
                        color
                    ))
                } else {
                    onAction(ListAction.CreatePixCategory(
                        name!!,
                        color!!
                    ))
                }
                showCategoryDialog = false
                categoryToEdit = null
            },
            onDelete = {
                onAction(ListAction.DeletePixCategory(categoryToEdit!!))
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
                onAction(ListAction.UpdatePixListName(newName))
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
                        Text(
                            text = state.curPixList?.name ?: "",
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.curPixList != null,
                        enter = scaleIn(tween(easing = EaseOutBack)),
                    ) {
                        IconButton({ showRenameDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename PixList",
                            )
                        }
                    }
                },
                openDrawer = { onAction(ListAction.OpenDrawer) },
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
            when {
                (state.listStatus == ListStatus.LOADING || state.curPixList != null) -> {
                    Row {
                        PixGrid(
                            entries = state.curPixList?.entries ?: emptyMap(),
                            enabled = state.curCategories.isNotEmpty(),
                            onEntryEdit = { date, categories ->
                                entryToEdit = date
                                curEntryCategories = categories
                                showEntryDialog = true
                            },
                            modifier = Modifier.weight(0.8f),
                        )
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
                                onAction(ListAction.UpdatePixCategoryOrder(it))
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
                }
                else -> {
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
}