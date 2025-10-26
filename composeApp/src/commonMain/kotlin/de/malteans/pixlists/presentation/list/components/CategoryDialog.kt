package de.malteans.pixlists.presentation.list.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.domain.PixColor
import de.malteans.pixlists.presentation.components.CustomDialog
import de.malteans.pixlists.presentation.components.Dropdown
import de.malteans.pixlists.presentation.components.customIcons.FilledPixIcon
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (String?, PixColor?, Boolean) -> Unit,
    onDelete: () -> Unit,
    colors: List<PixColor> = emptyList(),
    invalidNames: List<String> = emptyList(),
    isEdit: Boolean = false,
    categoryToEdit: PixCategory? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    var nameField by remember {
        mutableStateOf(
            TextFieldValue(
                text = categoryToEdit?.name ?: "",
                selection = TextRange((categoryToEdit?.name ?: "").length)
            )
        )
    }
    var color by remember { mutableStateOf(categoryToEdit?.color ?: colors.firstOrNull()) }

    CustomDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) stringResource(Res.string.edit_category)
                    else stringResource(Res.string.add_category),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        leftIcons = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        rightIcons = {
            val animationDuration = 150
            val enterAnimation = scaleIn(
                animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDuration),
            )
            val exitAnimation = scaleOut(
                animationSpec = tween(durationMillis = animationDuration),
            )
            val showDelete by remember {
                derivedStateOf {
                    nameField.text.trim() == (categoryToEdit?.name ?: "") && color == categoryToEdit?.color
                }
            }
            AnimatedVisibility(
                visible = !showDelete,
                enter = enterAnimation,
                exit = exitAnimation,
            ) {
                val enabled = nameField.text.isNotBlank() && color != null &&
                        (!invalidNames.contains(nameField.text.trim()) xor (nameField.text.trim() == (categoryToEdit?.name ?: "")))
                IconButton(
                    onClick = {
                        onSubmit(
                            if (nameField.text.trim() == (categoryToEdit?.name ?: "")) null else nameField.text,
                            if (color == categoryToEdit?.color) null else color,
                            isEdit
                        )
                    },
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Submit",
                        tint = if (enabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    )
                }
            }
            AnimatedVisibility(
                visible = showDelete,
                enter = enterAnimation,
                exit = exitAnimation,
            ) {
                var deleteClicked by remember { mutableStateOf(false) }
                LaunchedEffect(deleteClicked) {
                    if (deleteClicked) {
                        delay(2000)
                        deleteClicked = false
                    }
                }
                IconButton(
                    onClick = {
                        if (deleteClicked) onDelete()
                        deleteClicked = !deleteClicked
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (deleteClicked) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
        )
    ) {
        val invalid = invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (categoryToEdit?.name ?: "")
        AnimatedVisibility(
            visible = invalid,
            enter = expandVertically(),
            exit = shrinkVertically(),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                )
                Text(
                    text = stringResource(Res.string.name_already_in_use),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nameField,
                onValueChange = { nameField = it.copy(text = it.text.replace("\n", " ")) },
                singleLine = true,
                label = { Text(stringResource(Res.string.name)) },
                isError = invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (categoryToEdit?.name ?: ""),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Dropdown(
                    modifier = Modifier
                        .fillMaxWidth(),
                    options = colors.associateBy({ it }, { it.name }),
                    label = stringResource(Res.string.color),
                    onValueChanged = { color = it as PixColor },
                    selectedOption = Pair(color, color?.name
                        ?: stringResource(Res.string.no_color)),
                    optionIcon = { color ->
                        if (color != null) {
                            color as PixColor
                            Icon(
                                imageVector = FilledPixIcon,
                                contentDescription = "Color",
                                tint = color.toColor(),
                            )
                        }
                    },
                )
            }
//            Column ( TODO: Implement custom color
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalAlignment = Alignment.End,
//                verticalArrangement = Arrangement.Center,
//            ) {
//                Icon(
//                    imageVector = Icons.Default.AddCircle,
//                    contentDescription = "Add Color",
//                    tint = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier
//                        .padding(top = 10.dp)
//                        .clickable {
//
//                        }
//                )
//            }
        }
    }
}