package de.malteans.pixlists.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.presentation.components.CustomTopBar
import de.malteans.pixlists.presentation.components.SnackbarManager
import de.malteans.pixlists.presentation.list.components.CategoryDialog
import de.malteans.pixlists.presentation.list.components.CategoryList
import de.malteans.pixlists.presentation.list.components.EntryDialog
import de.malteans.pixlists.presentation.list.components.ListStatus
import de.malteans.pixlists.presentation.list.components.PixGrid
import de.malteans.pixlists.presentation.list.components.RenamePixListDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.loading
import pixlists.composeapp.generated.resources.no_pixlist_selected
import pixlists.composeapp.generated.resources.select_pixlist_desc
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
    val scope = rememberCoroutineScope()

//    BackHandler {
//        viewModel.undoLastAction()
//    }

    var showEntryDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<LocalDate?>(null) }
    var curEntryCategory by remember { mutableStateOf<PixCategory?>(null) }

    if (showEntryDialog) {
        val startDate: LocalDate? = entryToEdit
        val curCategory: PixCategory? = curEntryCategory
        if (startDate != null) {
            entryToEdit = null
        }
        if (curCategory != null) {
            curEntryCategory = null
        }
        EntryDialog(
            categories = state.curCategories,
            onDismiss = { showEntryDialog = false },
            onEdit = { date, category ->
                onAction(ListAction.SetPixEntry(date, category))
                showEntryDialog = false
            },
            startDate = startDate
                ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            curCategory = curCategory
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
            onAdd = { name, color, isEdit ->
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
                    if (state.curPixList != null) {
                        Text(
                            text = state.curPixList.name,
                            modifier = Modifier
                                .clickable { showRenameDialog = true }
                        )
                    }
                },
                actions = {
                    val RES_SELECT_PIXLIST_DESC = stringResource(Res.string.select_pixlist_desc)
                    IconButton(
                        onClick = {
                            if (state.curPixList != null  && state.curCategories.isNotEmpty()) {
                                showEntryDialog = true
                            } else {
                                scope.launch {
                                    SnackbarManager.showSnackbar(
                                        message = RES_SELECT_PIXLIST_DESC,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Entry",
                            tint = if (state.curCategories.isNotEmpty() && state.listStatus == ListStatus.OPENED) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            }
                        )
                    }
                },
                openDrawer = { onAction(ListAction.OpenDrawer) },
            )
        }
    ) { pad ->
        Box (
            modifier = Modifier
                .padding(pad)
                .padding(start = 8.dp, end = 4.dp, bottom = 16.dp)
        ) {
            when {
                (state.listStatus == ListStatus.LOADING) -> {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(Res.string.loading))
                    }
                }
                (state.listStatus == ListStatus.OPENED && state.curPixList != null) -> {
                    Row {
                        PixGrid(
                            curPixList = state.curPixList,
                            enabled = state.curCategories.isNotEmpty(),
                            onEntryEdit = { date, category ->
                                entryToEdit = date
                                curEntryCategory = category
                                showEntryDialog = true
                            },
                            modifier = Modifier.weight(0.8f),
                        )
                        // Categories -----------------------------------------------------------------
                        CategoryList(
                            state.curCategories,
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
                        )
                    }
                }
                else -> {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(Res.string.no_pixlist_selected))
                    }
                }
            }
        }
    }
}