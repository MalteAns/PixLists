package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.core.presentation.theme.PixListsTheme
import kotlin.math.sqrt

@Composable
fun PixCellCanvas(
    entries: List<PixEntry>,
    maxWeights: Map<Long, Int> = emptyMap(),
    isToday: Boolean = false,
    enabled: Boolean = false,
    animation: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    cornerRadius: Dp = 3.dp,      // outer rounded corners
    innerSpacing: Dp = 0.dp,      // keep 0 for “no background in middle”
    cellSpacing: Dp = 3.dp,       // gap between day cells (prevents neighbor outlines touching)
    outlineWidth: Dp = 1.dp,    // outline width
    todayOutlineWidth: Dp = 2.dp, // outline width for “today” (if different)
    contentPadding: Dp = 1.dp,    // inset between outline and fill
) {
    val emptyOutline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val todayOutline = Color.Red
    val errorColor = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }

    val animatedScale = Animatable(1f)
    var entriesState by remember { mutableStateOf(entries) }

    LaunchedEffect(Unit) {
        if (!animation) return@LaunchedEffect
        animatedScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(200, easing = EaseOutBack),
        )
    }

    LaunchedEffect(entries) {
        if (!animation) {
            entriesState = entries
            return@LaunchedEffect
        }
        animatedScale.animateTo(
            targetValue = 0f,
            animationSpec = tween(100)
        )
        entriesState = entries
        animatedScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(200, easing = EaseOutBack),
        )
    }

    Canvas(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
            }
            .size(size)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        val pxSize = size.toPx()
        val ow = outlineWidth.toPx()
        val tdOw = todayOutlineWidth.toPx()
        val pad = contentPadding.toPx()
        val r = cornerRadius.toPx()
        val cellGap = cellSpacing.toPx()

        // Outer (used only for spacing from neighbors)
        val outlineInset = ow / 2f
        val outerLeft = outlineInset + cellGap / 2f
        val outerTop = outlineInset + cellGap / 2f
        val outerRight = pxSize - outlineInset - cellGap / 2f
        val outerBottom = pxSize - outlineInset - cellGap / 2f

        // Content area (fills + “same sized” outlines)
        val contentLeft = outerLeft + pad
        val contentTop = outerTop + pad
        val contentRight = outerRight - pad
        val contentBottom = outerBottom - pad
        val contentW = (contentRight - contentLeft).coerceAtLeast(0f)
        val contentH = (contentBottom - contentTop).coerceAtLeast(0f)

        fun colorOf(i: Int): Color {
            val entry = entriesState.getOrNull(i) ?: return errorColor
            val baseColor = entry.category.color?.toColor() ?: errorColor
            val maxWeight = maxWeights[entry.category.id]
            if (entry.category.enableWeight && entry.weight != null
                    && maxWeight != null && maxWeight > 0
            ) {
                val alpha = (entry.weight.toFloat() / maxWeight.toFloat()).coerceIn(0f, 1f)
                return baseColor.copy(alpha = alpha)
            }
            return baseColor
        }

        val contentRoundRect = RoundRect(
            left = contentLeft, top = contentTop,
            right = contentRight, bottom = contentBottom,
            cornerRadius = CornerRadius(r, r)
        )

        // Helper to draw the “today” outline either on content or outer rect
        fun drawTodayOutline() {
            if (!isToday) return
            drawRoundRect(
                color = todayOutline,
                style = Stroke(width = tdOw),
                topLeft = Offset(contentLeft, contentTop),
                size = Size(contentW, contentH),
                cornerRadius = CornerRadius(r, r)
            )
        }

        if (entriesState.isEmpty()) {
            // EMPTY → outline on CONTENT rect so it matches the filled size
            drawRoundRect(
                color = emptyOutline,
                style = Stroke(width = ow),
                topLeft = Offset(contentLeft, contentTop),
                size = Size(contentW, contentH),
                cornerRadius = CornerRadius(r, r)
            )
            drawTodayOutline()
        } else {
            // Draw pizza slices - equal distribution for all categories
            clipPath(Path().apply { addRoundRect(contentRoundRect) }) {
                val centerX = contentLeft + contentW / 2f
                val centerY = contentTop + contentH / 2f
                // Use the diagonal as radius to reach the corners of the square
                val radius = sqrt(contentW * contentW + contentH * contentH) / 2f
                
                val sliceAngle = 360f / entriesState.size
                
                entriesState.forEachIndexed { index, _ ->
                    val startAngle = index * sliceAngle - 90f  // Start from top (12 o'clock)
                    val sweepAngle = sliceAngle
                    
                    // Draw pie slice with bounding box centered and large enough to reach corners
                    drawArc(
                        color = colorOf(index),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                }
            }
            drawTodayOutline()
        }
    }
}

// Preview helpers

private val previewColors = listOf(
    PixColor(id = 1L, name = "Red", red = 1f, green = 0.3f, blue = 0.3f),
    PixColor(id = 2L, name = "Green", red = 0.3f, green = 1f, blue = 0.3f),
    PixColor(id = 3L, name = "Blue", red = 0.3f, green = 0.3f, blue = 1f),
    PixColor(id = 4L, name = "Yellow", red = 1f, green = 1f, blue = 0.3f),
    PixColor(id = 5L, name = "Magenta", red = 1f, green = 0.3f, blue = 1f),
    PixColor(id = 6L, name = "Cyan", red = 0.3f, green = 1f, blue = 1f),
)

private fun createPreviewEntries(count: Int): List<PixEntry> {
    return (0 until count).map { index ->
        PixEntry(
            category = PixCategory(
                id = index.toLong(),
                listId = 1L,
                color = previewColors.getOrNull(index) ?: previewColors[0],
                name = "Category ${index + 1}",
            )
        )
    }
}

// Previews

@Preview
@Composable
fun PixCellCanvasPreview0Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = emptyList(),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview1Entry() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(1),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview2Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(2),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview3Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(3),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview4Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(4),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview5Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(5),
            animation = false,
        )
    }
}

@Preview
@Composable
fun PixCellCanvasPreview6Entries() {
    PixListsTheme {
        PixCellCanvas(
            entries = createPreviewEntries(6),
            animation = false,
        )
    }
}
