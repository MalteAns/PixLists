package de.malteans.pixlists.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    modifier: Modifier,
    label: String,
    selectedOption: Pair<Any?, String>,
    options: Map<Any, String>,
    onValueChanged: (Any) -> Unit,
    optionIcon: @Composable ((Any?) -> Unit)? = null,
    initialExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(initialExpanded) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption.second,
            onValueChange = { },
            leadingIcon = if (optionIcon != null) { { optionIcon(selectedOption.first) } }
                else null,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            label = { Text(label) },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth(),
            singleLine = true,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            options.toList().forEach { pair ->
                val (option, text) = pair
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        expanded = false
                        onValueChanged(option)
                    },
                    leadingIcon = if (optionIcon != null) { { optionIcon(option) } }
                        else null
                )
            }
        }
    }
}