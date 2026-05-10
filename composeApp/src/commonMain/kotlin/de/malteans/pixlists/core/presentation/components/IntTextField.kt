package de.malteans.pixlists.core.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun IntTextField(
    label: String,
    value: Int?,
    isError: Boolean = false,
    onValueChange: (Int?) -> Unit,
    allowRange: IntRange = 0..Int.MAX_VALUE,
    allowEmpty: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var currentValue by remember { mutableStateOf(value?.toString() ?: "") }
    OutlinedTextField(
        value = currentValue,
        onValueChange = {
            currentValue = it
            val intValue = it.toIntOrNull()
            if ((intValue != null && (intValue in allowRange)) || (allowEmpty && it.isEmpty())) {
                onValueChange(intValue)
            }
        },
        label = { Text(label) },
        singleLine = true,
        isError = isError || (currentValue.toIntOrNull() == null && (!allowEmpty || currentValue.isNotEmpty()))
                || (currentValue.toIntOrNull() != null && currentValue.toInt() !in allowRange),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .onFocusChanged {
                if (!allowEmpty && !it.isFocused) {
                    currentValue = value?.toString() ?: ""
                }
            }
    )
}