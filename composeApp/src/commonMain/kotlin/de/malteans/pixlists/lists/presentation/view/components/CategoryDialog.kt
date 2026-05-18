package de.malteans.pixlists.lists.presentation.view.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.presentation.components.CustomDialog
import de.malteans.pixlists.core.presentation.components.Dropdown
import de.malteans.pixlists.core.presentation.components.IntTextField
import de.malteans.pixlists.core.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.core.presentation.util.AnimatedDoubleIconButton
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.add_category
import pixlists.composeapp.generated.resources.color
import pixlists.composeapp.generated.resources.edit_category
import pixlists.composeapp.generated.resources.enable_weight
import pixlists.composeapp.generated.resources.max
import pixlists.composeapp.generated.resources.min
import pixlists.composeapp.generated.resources.name
import pixlists.composeapp.generated.resources.name_already_in_use
import pixlists.composeapp.generated.resources.no_color
import pixlists.composeapp.generated.resources.step
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        color: PixColor,
        enableWeight: Boolean,
        minWeight: Int,
        maxWeight: Int,
        weightStep: Int,
        isEdit: Boolean,
    ) -> Unit, // name, color, enableWeight, minWeight, maxWeight, weightStep, isEdit
    onDelete: () -> Unit,
    colors: List<PixColor> = emptyList(),
    invalidNames: List<String> = emptyList(),
    isEdit: Boolean = false,
    categoryToEdit: PixCategory? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
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

    var enableWeight by remember { mutableStateOf(categoryToEdit?.enableWeight ?: PixCategory.DEFAULT_ENABLE_WEIGHT) }
    var minWeight by remember { mutableStateOf(categoryToEdit?.minWeight ?: PixCategory.DEFAULT_MIN_WEIGHT) }
    var maxWeight by remember { mutableStateOf(categoryToEdit?.maxWeight ?: PixCategory.DEFAULT_MAX_WEIGHT) }
    var weightStep by remember { mutableStateOf(categoryToEdit?.weightStep ?: PixCategory.DEFAULT_WEIGHT_STEP) }

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
            val showDelete by remember { derivedStateOf {
                nameField.text.trim() == (categoryToEdit?.name ?: "") &&
                color == categoryToEdit?.color &&
                enableWeight == (categoryToEdit?.enableWeight) &&
                minWeight == (categoryToEdit.minWeight) &&
                maxWeight == (categoryToEdit.maxWeight) &&
                weightStep == (categoryToEdit.weightStep)
            } }
            val validToSubmit by remember { derivedStateOf {
                nameField.text.isNotBlank() &&
                color != null &&
                (!invalidNames.contains(nameField.text.trim())
                    xor (nameField.text.trim() == (categoryToEdit?.name ?: ""))) &&
                (minWeight < maxWeight) &&
                (weightStep > 0)
            } }
            var deleteClicked by remember { mutableStateOf(false) }
            LaunchedEffect(deleteClicked) {
                if (deleteClicked) {
                    delay(2.seconds)
                    deleteClicked = false
                }
            }
            AnimatedDoubleIconButton(
                showSecondary = showDelete,
                enabled = showDelete || validToSubmit,
                onClick = {
                    if (showDelete) {
                        if (deleteClicked) onDelete()
                        deleteClicked = !deleteClicked
                    } else onSubmit(
                        nameField.text,
                        color!!,
                        enableWeight,
                        minWeight,
                        maxWeight,
                        weightStep,
                        isEdit
                    )
                },
                secondaryIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (deleteClicked) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                    )
                }
            )
        },
    ) {
        // Name selection -------------------------------------------------------------------------
        val invalidName by remember(invalidNames, nameField.text, categoryToEdit) { derivedStateOf {
            invalidNames.contains(nameField.text.trim()) && nameField.text.trim() != (categoryToEdit?.name ?: "")
        } }
        if (invalidName) {
            Row(Modifier.fillMaxWidth()) {
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
        OutlinedTextField(
            value = nameField,
            onValueChange = { nameField = it.copy(text = it.text.replace("\n", " ")) },
            singleLine = true,
            label = { Text(stringResource(Res.string.name)) },
            isError = invalidName,
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        // Color selection ------------------------------------------------------------------------
        val colorOptions by remember {
            mutableStateOf(colors.sortedBy { it.name }.associateBy({ it }, { it.name }))
        }
        Dropdown(
            options = colorOptions,
            label = stringResource(Res.string.color),
            onValueChanged = { color = it },
            selectedOption = Pair(color, color?.name ?: stringResource(Res.string.no_color)),
            optionIcon = { color ->
                if (color != null) {
                    Icon(
                        imageVector = FilledPixIcon,
                        contentDescription = "Color",
                        tint = color.toColor(),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(Res.string.enable_weight), modifier = Modifier.weight(1f))
            Switch(
                checked = enableWeight,
                onCheckedChange = { enableWeight = it }
            )
        }

        if (enableWeight) {
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IntTextField(
                    label = stringResource(Res.string.min),
                    value = minWeight,
                    allowRange = 0 until maxWeight.coerceAtLeast(0),
                    onValueChange = { minWeight = it!! },
                    modifier = Modifier.weight(1f)
                )
                IntTextField(
                    label = stringResource(Res.string.max),
                    value = maxWeight,
                    allowRange = (minWeight + 1)..Int.MAX_VALUE,
                    onValueChange = { maxWeight = it!! },
                    modifier = Modifier.weight(1f)
                )
                IntTextField(
                    label = stringResource(Res.string.step),
                    value = weightStep,
                    allowRange = 1..Int.MAX_VALUE,
                    onValueChange = { weightStep = it!! },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}