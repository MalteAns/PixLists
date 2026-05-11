package de.malteans.pixlists.core.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconIconButton(
    imageVector: ImageVector,
    contentDescription: String? = null,
    iconColor: Color = LocalContentColor.current,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconColor,
        )
    }
}