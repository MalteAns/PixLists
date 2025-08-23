package de.malteans.pixlists.presentation.main

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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import de.malteans.pixlists.app.Route
import de.malteans.pixlists.domain.PixList
import de.malteans.pixlists.presentation.components.CustomDialog
import de.malteans.pixlists.presentation.components.SnackbarManager
import de.malteans.pixlists.presentation.components.customIcons.AddPixListIcon
import de.malteans.pixlists.presentation.components.customIcons.FilledPixListIcon
import de.malteans.pixlists.presentation.components.customIcons.OutlinedPixListIcon
import de.malteans.pixlists.presentation.main.components.NavGraph
import de.malteans.pixlists.presentation.main.components.NavListHeader
import de.malteans.pixlists.presentation.main.components.NewListDialog
import de.malteans.pixlists.presentation.main.components.Screen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.confirm_delete_desc
import pixlists.composeapp.generated.resources.delete_pixlist
import pixlists.composeapp.generated.resources.manage_colors
import pixlists.composeapp.generated.resources.new_pixlist
import pixlists.composeapp.generated.resources.settings

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
                scope.launch {
                    showNewListDialog = false
                    viewModel.setCurScreen(Screen.LIST)
                    navController.navigate(Route.LoadingScreen)
                    val id = viewModel.createPixList(name.trim())
                    viewModel.setCurPixListId(id)
                    drawerState.close()
                    navController.navigate(Route.ListScreen(id))
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
                            navController.navigate(Route.ListScreen(listToDelete!!.id))
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
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(8.dp))
                    NavListHeader(
                        onLongClick = {
                            showHiddenLists = !showHiddenLists
                            scope.launch {
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
                        items(state.allPixLists) { curPixList ->
                            if (!(curPixList.name.matches(Regex("^\\(.*\\)$")) && !showHiddenLists)) {
                                NavigationDrawerItem(
                                    label = { Text(curPixList.name) },
                                    selected = curPixList.id == state.curPixListId,
                                    onClick = {
                                        viewModel.setCurScreen(Screen.LIST)
                                        viewModel.setCurPixListId(curPixList.id)
                                        scope.launch {
                                            navController.navigate(Route.LoadingScreen)
                                            drawerState.close()
                                            navController.navigate(Route.ListScreen(curPixList.id)) {
                                                popUpTo(Route.LoadingScreen) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                    icon = {
                                        Icon(
                                            imageVector = if (curPixList.id == state.curPixListId)
                                                FilledPixListIcon
                                            else
                                                OutlinedPixListIcon,
                                            contentDescription = "PixList"
                                        )
                                    },
                                    badge = {
                                        IconButton(
                                            onClick = {
                                                listToDelete = curPixList
                                                if (curPixList.id == state.curPixListId) {
                                                    navController.navigate(Route.ListScreen(null))
                                                }
                                                showDeleteListDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete PixList"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        item {
                            NavigationDrawerItem(
                                label = { Text(stringResource(Res.string.new_pixlist)) },
                                onClick = {
                                    showNewListDialog = true
                                },
                                selected = false,
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                icon = {
                                    Icon(
                                        imageVector = AddPixListIcon,
                                        contentDescription = "Add PixList"
                                    )
                                }
                            )
                        }
                    }
                    NavigationDrawerItem(
                        label = { Text(stringResource(Res.string.manage_colors)) },
                        onClick = {
                            navController.navigate(Route.ManageColorsScreen)
                            viewModel.setCurPixListId(null)
                            viewModel.setCurScreen(Screen.MANAGE_COLORS)
                            scope.launch { drawerState.close() }
                        },
                        selected = state.curScreen == Screen.MANAGE_COLORS,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = {
                            if (state.curScreen == Screen.MANAGE_COLORS) {
                                Icon(
                                    imageVector = Icons.Filled.ColorLens,
                                    contentDescription = "ColorLens"
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.ColorLens,
                                    contentDescription = "ColorLens"
                                )
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(Res.string.settings)) },
                        onClick = {
                            navController.navigate(Route.SettingsScreen)
                            viewModel.setCurPixListId(null)
                            viewModel.setCurScreen(Screen.SETTINGS)
                            scope.launch { drawerState.close() }
                        },
                        selected = state.curScreen == Screen.SETTINGS,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = {
                            if (state.curScreen == Screen.SETTINGS) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings"
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    )
                }
            },
        ) {
            NavGraph(
                navController = navController,
                openDrawer = { scope.launch { drawerState.open() } },
                setCurScreen = { screen -> viewModel.setCurScreen(screen) },
                setPixListId = { id -> viewModel.setCurPixListId(id) },
            )
        }
    }
}