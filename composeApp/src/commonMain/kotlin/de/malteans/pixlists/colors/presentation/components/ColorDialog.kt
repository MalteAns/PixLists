package de.malteans.pixlists.colors.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.core.presentation.components.customIcons.OutlinedPixIcon
import de.malteans.pixlists.core.presentation.theme.containerColor
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*

@Composable
fun ColorDialog(
    onDismiss: () -> Unit,
    onSubmit: (newName: String?, newRgb: List<Float>?, Boolean) -> Unit,
    onDelete: () -> Unit,
    invalidNames: List<String> = emptyList(),
    isEdit: Boolean = false,
    colorToEdit: PixColor? = null,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (!isEdit) {
            delay(150)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    var nameField by remember {
        mutableStateOf(
            TextFieldValue(
                text = colorToEdit?.name ?: "",
                selection = TextRange((colorToEdit?.name ?: "").length)
            )
        )
    }
    var selectedHexField by remember {
        mutableStateOf(
            TextFieldValue(
                text = colorToEdit?.toHex() ?: "",
                selection = TextRange((colorToEdit?.toHex() ?: "").length)
            )
        )
    }
    var selectedRgbValues by remember { mutableStateOf(colorToEdit?.getRgbValues()) }

    CustomDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) stringResource(Res.string.edit_color)
                    else stringResource(Res.string.add_color),
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

            val unsavedChanges by remember { derivedStateOf {
                nameField.text.trim() != (colorToEdit?.name ?: "")
                    ||
                selectedRgbValues != (colorToEdit?.getRgbValues())
            } }

            val validToSave by remember { derivedStateOf {
                unsavedChanges
                    &&
                nameField.text.isNotBlank() &&
                        (!invalidNames.contains(nameField.text.trim()) xor (nameField.text.trim() == (colorToEdit?.name ?: "")))
                    &&
                selectedRgbValues?.all { it in 0..255 } == true
            } }

            val showDelete by remember { derivedStateOf {
                isEdit && colorToEdit != null
                    &&
                !unsavedChanges
            } }

            var deleteClicked by remember { mutableStateOf(false) }
            LaunchedEffect(deleteClicked) {
                if (deleteClicked) {
                    delay(2000)
                    deleteClicked = false
                }
            }

            IconButton(
                onClick = {
                    if (showDelete) {
                        if (deleteClicked) onDelete()
                        deleteClicked = !deleteClicked
                    } else onSubmit(
                        if (nameField.text.trim() == (colorToEdit?.name ?: "")) null else nameField.text,
                        selectedRgbValues?.toFloatColorValues(),
                        isEdit,
                    )
                },
                enabled = showDelete || validToSave,
            ) {
                AnimatedVisibility(
                    visible = !showDelete, enter = enterAnimation, exit = exitAnimation,
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Done",
                        tint = selectedRgbValues.takeIf { validToSave }?.toColor()
                            ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                }
                AnimatedVisibility(
                    visible = showDelete,
                    enter = enterAnimation,
                    exit = exitAnimation,
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
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        val focusManager = LocalFocusManager.current
        if (invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (colorToEdit?.name ?: "")) {
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
                onValueChange = { nameField = it },
                singleLine = true,
                label = { Text(stringResource(Res.string.name)) },
                isError = nameField.text.isBlank() || invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (colorToEdit?.name ?: ""),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = selectedHexField,
            onValueChange = { newValue ->
                if (newValue.text.length <= 6 &&
                        newValue.text.all { it.isDigit() || it.uppercaseChar() in 'A'..'F' })
                    selectedHexField = newValue
                if (newValue.text.isValidHexColor())
                    selectedRgbValues = newValue.text.hexToRgb()
            },
            singleLine = true,
            label = { Text(stringResource(Res.string.color)) },
            isError = !selectedHexField.text.isValidHexColor(),
            prefix = { Text("#") },
            trailingIcon = {
                if (selectedHexField.text.isValidHexColor()) {
                    val tempRbgValues = selectedHexField.text.hexToRgb().toFloatColorValues()
                    Icon(
                        imageVector = FilledPixIcon,
                        contentDescription = "Preview",
                        tint = Color(red = tempRbgValues[0], green = tempRbgValues[1], blue = tempRbgValues[2]),
                    )
                } else {
                    Icon(
                        imageVector = OutlinedPixIcon,
                        contentDescription = "Preview",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Slider(
            value = selectedRgbValues?.get(0)?.let { it / 255f } ?: 0f,
            onValueChange = { newValue ->
                focusManager.clearFocus()
                selectedRgbValues = listOf(
                    (newValue * 255).toInt(),
                    selectedRgbValues?.get(1) ?: 0,
                    selectedRgbValues?.get(2) ?: 0,
                )
                selectedHexField = TextFieldValue(
                    text = selectedRgbValues!!.joinToString("") { it.toHex() },
                    selection = TextRange(6)
                )
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Red,
                activeTrackColor = Color.Red,
                inactiveTrackColor = MaterialTheme.colorScheme.containerColor,
            ),
        )
        Slider(
            value = selectedRgbValues?.get(1)?.let { it / 255f } ?: 0f,
            onValueChange = { newValue ->
                focusManager.clearFocus()
                selectedRgbValues = listOf(
                    selectedRgbValues?.get(0) ?: 0,
                    (newValue * 255).toInt(),
                    selectedRgbValues?.get(2) ?: 0,
                )
                selectedHexField = TextFieldValue(
                    text = selectedRgbValues!!.joinToString("") { it.toHex() },
                    selection = TextRange(6)
                )
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTrackColor = Color.Green,
                inactiveTrackColor = MaterialTheme.colorScheme.containerColor,
            ),
        )
        Slider(
            value = selectedRgbValues?.get(2)?.let { it / 255f } ?: 0f,
            onValueChange = { newValue ->
                focusManager.clearFocus()
                selectedRgbValues = listOf(
                    selectedRgbValues?.get(0) ?: 0,
                    selectedRgbValues?.get(1) ?: 0,
                    (newValue * 255).toInt(),
                )
                selectedHexField = TextFieldValue(
                    text = selectedRgbValues!!.joinToString("") { it.toHex() },
                    selection = TextRange(6)
                )
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Blue,
                activeTrackColor = Color.Blue,
                inactiveTrackColor = MaterialTheme.colorScheme.containerColor,
            ),
        )
    }
}

private fun String.isValidHexColor(): Boolean {
    val regex = Regex("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    return regex.matches(this)
}

private fun Int.toHex() = this.toString(16).uppercase().padStart(2, '0')

private fun String.hexToRgb(): List<Int> {
    var red: Int
    var green: Int
    var blue: Int
    when (this.length) {
        6 -> {
            red = this.substring(0, 2).toInt(16)
            green = this.substring(2, 4).toInt(16)
            blue = this.substring(4, 6).toInt(16)
        }
        3 -> {
            red = this[0].toString().toInt(16) * 17
            green = this[1].toString().toInt(16) * 17
            blue = this[2].toString().toInt(16) * 17
        }
        else -> throw IllegalArgumentException("String must be a valid hex color code. (length with # must be 4 or 7)")
    }
    return listOf(red, green, blue)
}

private fun List<Int>.toFloatColorValues(): List<Float> {
    return this.map { it / 255f }
}

private fun List<Int>.toColor(): Color {
    val floatValues = this.toFloatColorValues()
    return Color(
        red = floatValues[0],
        green = floatValues[1],
        blue = floatValues[2],
    )
}