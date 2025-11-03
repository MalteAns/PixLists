package de.malteans.pixlists.colors.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.customIcons.FilledPixIcon
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*

@Composable
fun ColorDialog(
    onDismiss: () -> Unit,
    onFinish: (newName: String?, newRgb: List<Float>?, Boolean) -> Unit,
    onDelete: () -> Unit,
    invalidNames: List<String> = emptyList(),
    isEdit: Boolean = false,
    colorToEdit: PixColor? = null,
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
                text = colorToEdit?.name ?: "",
                selection = TextRange((colorToEdit?.name ?: "").length)
            )
        )
    }
    var selectedHexField by remember {
        mutableStateOf(
            TextFieldValue(
                text = colorToEdit?.toHex() ?: "#",
                selection = TextRange((colorToEdit?.toHex() ?: "#").length)
            )
        )
    }
    val selectedRgbValues by remember { mutableStateOf(colorToEdit?.getRgbValues() ?: listOf(0f, 0f, 0f)) }
    val mode by remember { mutableStateOf(Mode.HEX) }

    var validToSave by remember { mutableStateOf(false) }

    LaunchedEffect(nameField.text, selectedHexField.text, selectedRgbValues, mode) {
        validToSave = nameField.text.isNotBlank() &&
            (!invalidNames.contains(nameField.text.trim()) xor (nameField.text.trim() == (colorToEdit?.name ?: "")) &&
            (mode == Mode.HEX && isValidHexColor(selectedHexField.text) &&
                (!isEdit || selectedHexField.text != (colorToEdit?.toHex() ?: "") || nameField.text.trim() != (colorToEdit?.name ?: "")) ||
            mode == Mode.RGB &&
                (!isEdit || selectedRgbValues != (colorToEdit?.getRgbValues() ?: listOf<Float>()) || nameField.text.trim() != (colorToEdit?.name ?: ""))))
    }

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
            if (isEdit) {
                IconButton(
                    onClick = { onDelete() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        rightIcons = {
            IconButton(
                onClick = {
                    onFinish(
                        if (nameField.text.trim() == (colorToEdit?.name ?: "")) null else nameField.text,
                        if (mode == Mode.HEX) {
                            if (selectedHexField.text == (colorToEdit?.toHex() ?: "")) null else hexToRgb(selectedHexField.text)
                        } else if (mode == Mode.RGB) {
                            if (selectedRgbValues == (colorToEdit?.getRgbValues() ?: listOf<Float>())) null else selectedRgbValues
                        } else null,
                        isEdit,
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done",
                    tint = if (validToSave) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            }
        },
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
                isError = invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (colorToEdit?.name ?: ""),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier
                    .then(
                        if (colorToEdit == null) Modifier.focusRequester(focusRequester)
                        else Modifier
                    )
                    .fillMaxWidth()
            )
        }
        // TODO: Add mode switcher
        when (mode) {
            Mode.HEX -> {                
                OutlinedTextField(
                    value = selectedHexField,
                    onValueChange = { newValue ->
                        if (newValue.text.length in 1..7
                            && newValue.text.startsWith("#")
                            && newValue.text.drop(1).all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
                        ) {
                            selectedHexField = newValue
                        }
                    },
                    singleLine = true,
                    label = { Text(stringResource(Res.string.color)) },
                    isError = !isValidHexColor(selectedHexField.text),
                    trailingIcon = {
                        if (isValidHexColor(selectedHexField.text)) {
                            val tempRbgValues = hexToRgb(selectedHexField.text)
                            Icon(
                                imageVector = FilledPixIcon,
                                contentDescription = "Preview",
                                tint = Color(red = tempRbgValues[0], green = tempRbgValues[1], blue = tempRbgValues[2]),
                            )
                        }
                    },
                    modifier = Modifier
                        .then(
                            if (colorToEdit != null) Modifier.focusRequester(focusRequester)
                            else Modifier
                        )
                        .fillMaxWidth()
                )
            }
            Mode.RGB -> {
                // TODO: Add RGB input fields
            }
        }
    }
}

fun isValidHexColor(hex: String): Boolean {
    val regex = Regex("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    return regex.matches(hex)
}

fun hexToRgb(hexString: String): List<Float> {
    var red = 0f
    var green = 0f
    var blue = 0f
    when (hexString.length) {
        7 -> {
            red = hexString.substring(1, 3).toInt(16) / 255f
            green = hexString.substring(3, 5).toInt(16) / 255f
            blue = hexString.substring(5, 7).toInt(16) / 255f
        }
        4 -> {
            red = hexString.substring(1, 2).toInt(16) / 15f
            green = hexString.substring(2, 3).toInt(16) / 15f
            blue = hexString.substring(3, 4).toInt(16) / 15f
        }
    }
    return listOf(red, green, blue)
}

enum class Mode {
    HEX, RGB
}

