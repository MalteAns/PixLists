package de.malteans.pixlists.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Dropdown(
    label: String? = null,
    selectedOption: Pair<T?, String>,
    options: Map<T, String>,
    optionIcon: @Composable ((T?) -> Unit)? = null,
    initialExpanded: Boolean = false,
    onValueChanged: (T) -> Unit,
    modifier: Modifier= Modifier,
) {
    val focusManager = LocalFocusManager.current

    var expanded by remember { mutableStateOf(initialExpanded) }

    val onExpandedChange: (Boolean) -> Unit = { isExpanded ->
        expanded = isExpanded
        if (!isExpanded) {
            focusManager.clearFocus()
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) },
        modifier = modifier
    ) {
        val contentColor = LocalContentColor.current
        val unfocusedContentColor = contentColor.copy(alpha = 0.7f)
        val unfocusedBorderColor = contentColor.copy(alpha = 0.5f)

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor,
            focusedLeadingIconColor = contentColor,
            unfocusedLeadingIconColor = unfocusedContentColor,
            focusedTrailingIconColor = contentColor,
            unfocusedTrailingIconColor = unfocusedContentColor,
            focusedLabelColor = contentColor,
            unfocusedLabelColor = unfocusedContentColor,
            focusedBorderColor = contentColor,
            unfocusedBorderColor = unfocusedBorderColor,
        )

        OutlinedTextField(
            readOnly = true,
            value = selectedOption.second,
            onValueChange = { },
            colors = textFieldColors,
            leadingIcon = optionIcon?.let { { optionIcon(selectedOption.first) } },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            label = label?.let { { Text(it) } },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            options.toList().forEach { pair ->
                val (option, text) = pair
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        onExpandedChange(false)
                        onValueChanged(option)
                    },
                    leadingIcon = if (optionIcon != null) { { optionIcon(option) } }
                        else null
                )
            }
        }
    }
}