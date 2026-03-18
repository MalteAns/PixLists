package de.malteans.pixlists.core.presentation.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AnimatedDoubleIconButton(
    showSecondary: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    primaryIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Submit",
            tint = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        )
    },
    secondaryIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = if (enabled) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
        )
    },
) {
    val animationDuration = 150
    val enterAnimation = scaleIn(
        animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDuration),
    )
    val exitAnimation = scaleOut(
        animationSpec = tween(durationMillis = animationDuration),
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        AnimatedVisibility(
            visible = !showSecondary, enter = enterAnimation, exit = exitAnimation,
        ) {
            primaryIcon()
        }
        AnimatedVisibility(
            visible = showSecondary, enter = enterAnimation, exit = exitAnimation,
        ) {
            secondaryIcon()
        }
    }
}