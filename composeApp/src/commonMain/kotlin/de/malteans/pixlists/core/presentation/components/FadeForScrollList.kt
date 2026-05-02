package de.malteans.pixlists.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FadeForScrollList(
    showTopFade: Boolean,
    showBottomFade: Boolean,
    fadeHeight: Dp = 40.dp,
    fadeColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = listOf(
        fadeColor.copy(alpha = 0.0f),
        fadeColor.copy(alpha = 0.5f),
        fadeColor.copy(alpha = 0.8f),
    )
    Box(modifier) {
        content()
        // Top fade
        if (showTopFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = colors.reversed()
                        )
                    )
            )
        }
        // Bottom fade
        if (showBottomFade) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = colors
                        )
                    )
            )
        }
    }
}