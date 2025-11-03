package de.malteans.pixlists.colors.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.pixlists.colors.presentation.components.ColorDialog
import de.malteans.pixlists.colors.presentation.components.ColorItem
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.CustomTopBar
import de.malteans.pixlists.core.presentation.main.components.StartColorsDialog
import de.malteans.pixlists.core.presentation.main.components.defaultStartColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pixlists.composeapp.generated.resources.*

@Composable
fun ManageColorsScreenRoot(
    viewModel: ManageColorsViewModel = koinViewModel(),
    openDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ManageColorsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ManageColorsAction.OpenDrawer -> openDrawer()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManageColorsScreen(
    state: ManageColorsState,
    onAction: (ManageColorsAction) -> Unit,
) {
    var showColorDialog by remember { mutableStateOf(false) }
    var colorToEdit by remember { mutableStateOf<PixColor?>(null) }
    if (showColorDialog) {
        ColorDialog(
            onDismiss = {
                showColorDialog = false
                colorToEdit = null
            },
            onFinish = { newName, newRgb, isEdit ->
                if (isEdit) {
                    onAction(
                        ManageColorsAction.UpdateColor(
                            colorToEdit = colorToEdit!!,
                            newName = newName,
                            newRgb = newRgb,
                        )
                    )
                } else {
                    onAction(
                        ManageColorsAction.AddColor(
                            name = newName!!,
                            red = newRgb!![0],
                            green = newRgb[1],
                            blue = newRgb[2],
                        )
                    )
                }
                showColorDialog = false
                colorToEdit = null
            },
            onDelete = {
                colorToEdit?.let { onAction(ManageColorsAction.DeleteColor(it)) }
                showColorDialog = false
                colorToEdit = null
            },
            invalidNames = state.colorList.map { it.name },
            isEdit = colorToEdit != null,
            colorToEdit = colorToEdit
        )
    }

    var showStartColorsDialog by remember { mutableStateOf(false) }
    if (showStartColorsDialog) {
        StartColorsDialog(
            onDismissRequest = { showStartColorsDialog = false },
            onSubmit = { colors -> onAction(ManageColorsAction.AddColors(colors)) },
            availableColors = defaultStartColors.filter { color ->
                !state.colorList.any { existingColor ->
                    existingColor.name == color.name
                        ||
                    (existingColor.red == color.red &&
                        existingColor.green == color.green &&
                        existingColor.blue == color.blue)
                }
            }
        )
    }

    var showDeleteUnusedDialog by remember { mutableStateOf(false) }
    if (showDeleteUnusedDialog) {
        CustomDialog(
            onDismissRequest = { showDeleteUnusedDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.delete_unused_colors),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            leftIcons = {
                IconButton({ showDeleteUnusedDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Close",
                    )
                }
            },
            rightIcons = {
                IconButton({
                    onAction(ManageColorsAction.DeleteUnusedColors)
                    showDeleteUnusedDialog = false
                }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        ) {
            Text(
                text = stringResource(Res.string.delete_unused_colors_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = { Text(stringResource(Res.string.manage_colors)) },
                actions = {
                    IconButton({ showDeleteUnusedDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = "Delete Unused Colors",
                        )
                    }
                },
                openDrawer = { onAction(ManageColorsAction.OpenDrawer) }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SmallFloatingActionButton({ showStartColorsDialog = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add_notes),
                        contentDescription = "Add Default Colors",
                        modifier = Modifier.size(24.dp)
                    )
                }
                FloatingActionButton({ showColorDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Color",
                    )
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.colorList) { color ->
                    ColorItem(
                        color = color,
                        count = state.colorUses[color.id] ?: 0,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                colorToEdit = color
                                showColorDialog = true
                            }
                    )
                }
            }
        }
    }
}

