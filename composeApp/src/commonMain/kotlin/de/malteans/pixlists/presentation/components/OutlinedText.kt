package de.malteans.pixlists.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedText(
    value: String,
    label: @Composable () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    OutlinedTextFieldDefaults.DecorationBox(
        value = value,
        innerTextField = {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        },
        enabled = true,
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        interactionSource = remember { MutableInteractionSource() },
        label = label,
    )
}