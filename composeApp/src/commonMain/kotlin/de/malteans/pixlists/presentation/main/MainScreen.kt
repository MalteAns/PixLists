package de.malteans.pixlists.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import de.malteans.pixlists.domain.PixList
import de.malteans.pixlists.navigation.NavGraph
import de.malteans.pixlists.navigation.Route
import de.malteans.pixlists.presentation.components.CustomDialog
import de.malteans.pixlists.presentation.components.SnackbarManager
import de.malteans.pixlists.presentation.components.customIcons.AddPixListIcon
import de.malteans.pixlists.presentation.components.customIcons.FilledPixListIcon
import de.malteans.pixlists.presentation.components.customIcons.OutlinedPixListIcon
import de.malteans.pixlists.presentation.main.components.CurScreen
import de.malteans.pixlists.presentation.main.components.CustomDrawerItem
import de.malteans.pixlists.presentation.main.components.NavListHeader
import de.malteans.pixlists.presentation.main.components.NewListDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.*

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    var showNewListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<PixList?>(null) }

    if (showNewListDialog) {
        NewListDialog(
            onDismiss = { showNewListDialog = false },
            onAdd = { name ->
                scope.launch(Dispatchers.Main) {
                    showNewListDialog = false
                    viewModel.setCurScreen(CurScreen.LIST)
                    navController.navigate(Route.List.Loading)
                    val id = viewModel.createPixList(name.trim())
                    viewModel.setCurPixListId(id)
                    drawerState.close()
                    navController.navigate(Route.List.View(id))
                }
            },
            invalidNames = state.allPixLists.map { it.name },
        )
    }

    if (showDeleteListDialog && listToDelete != null) {
        CustomDialog(
            onDismissRequest = { showDeleteListDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.delete_pixlist),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            leftIcons = {
                IconButton(
                    onClick = {
                        if (listToDelete!!.id == state.curPixListId) {
                            navController.navigate(Route.List.View(listToDelete!!.id))
                        }
                        showDeleteListDialog = false
                    }
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
                        viewModel.deletePixListById(listToDelete!!.id)
                        if (listToDelete!!.id == state.curPixListId) {
                            viewModel.setCurPixListId(null)
                        }
                        showDeleteListDialog = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            },
        ) {
            Text(
                text = stringResource(Res.string.confirm_delete_desc, listToDelete!!.name),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    var showHiddenLists by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.snackbarMessages.collect { snackbarValue ->
            val snackbarResult = snackbarHostState.showSnackbar(
                message = snackbarValue.message,
                actionLabel = snackbarValue.actionLabel,
                withDismissAction = snackbarValue.withDismissAction,
                duration = snackbarValue.duration
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                snackbarValue.onAction()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 32.dp)
        ) },
        modifier = Modifier.fillMaxSize(),
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = state.curScreen != CurScreen.LEGALS,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(8.dp))
                    NavListHeader(
                        onLongClick = {
                            showHiddenLists = !showHiddenLists
                            scope.launch(Dispatchers.IO) {
                                SnackbarManager.showSnackbar(
                                    message = "Hidden lists are now ${if (showHiddenLists) "visible" else "hidden"}",
                                    actionLabel = "Undo",
                                    onAction = { showHiddenLists = !showHiddenLists },
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            items = state.allPixLists
                                .filterNot { it.name.matches(Regex("^\\(.*\\)$")) && !showHiddenLists }
                        ) { curPixList ->
                            var appeared by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { appeared = true }

                            AnimatedVisibility(
                                visible = appeared,
                                enter = expandVertically(
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                )
                            ) {
                                CustomDrawerItem(
                                    icon = {
                                        Icon(
                                            imageVector = if (curPixList.id == state.curPixListId) FilledPixListIcon
                                            else OutlinedPixListIcon,
                                            contentDescription = "PixList"
                                        )
                                    },
                                    label = curPixList.name,
                                    badge = {
                                        IconButton(
                                            onClick = {
                                                listToDelete = curPixList
                                                if (curPixList.id == state.curPixListId) {
                                                    navController.navigate(Route.List.View(null))
                                                }
                                                showDeleteListDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete PixList"
                                            )
                                        }
                                    },
                                    selected = curPixList.id == state.curPixListId,
                                    onClick = {
                                        viewModel.setCurScreen(CurScreen.LIST)
                                        viewModel.setCurPixListId(curPixList.id)
                                        scope.launch(Dispatchers.Main) {
                                            navController.navigate(Route.List.Loading)
                                            drawerState.close()
                                            navController.navigate(Route.List.View(curPixList.id)) {
                                                popUpTo(Route.List.Loading) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        item {
                            CustomDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = AddPixListIcon,
                                        contentDescription = "Add PixList"
                                    )
                                },
                                label = stringResource(Res.string.new_pixlist),
                                selected = false,
                                onClick = { showNewListDialog = true }
                            )
                        }
                    }
                    CustomDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (state.curScreen == CurScreen.MANAGE_COLORS) Icons.Filled.ColorLens
                                    else Icons.Outlined.ColorLens,
                                contentDescription = "ColorLens"
                            )
                        },
                        label = stringResource(Res.string.manage_colors),
                        selected = state.curScreen == CurScreen.MANAGE_COLORS,
                        onClick = {
                            navController.navigate(Route.Colors.Overview)
                            viewModel.setCurPixListId(null)
                            viewModel.setCurScreen(CurScreen.MANAGE_COLORS)
                            scope.launch(Dispatchers.IO) { drawerState.close() }
                        }
                    )
                    CustomDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (state.curScreen == CurScreen.SETTINGS) Icons.Filled.Settings
                                else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = stringResource(Res.string.settings),
                        selected = state.curScreen == CurScreen.SETTINGS,
                        onClick = {
                            navController.navigate(Route.Settings.Overview)
                            viewModel.setCurPixListId(null)
                            viewModel.setCurScreen(CurScreen.SETTINGS)
                            scope.launch(Dispatchers.IO) { drawerState.close() }
                        },
                    )
                }
            },
        ) {
            NavGraph(
                navController = navController,
                openDrawer = { scope.launch(Dispatchers.IO) { drawerState.open() } },
                setCurState = { screen, id ->
                    viewModel.setCurScreen(screen)
                    viewModel.setCurPixListId(id)
                },
            )
        }
    }
}