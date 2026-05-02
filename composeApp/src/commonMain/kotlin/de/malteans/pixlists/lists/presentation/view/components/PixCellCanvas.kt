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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory

@Composable
fun PixCellCanvas(
    categories: List<PixCategory>,
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
    var categoriesState by remember { mutableStateOf(categories) }

    LaunchedEffect(Unit) {
        if (!animation) return@LaunchedEffect
        animatedScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(200, easing = EaseOutBack),
        )
    }

    LaunchedEffect(categories) {
        if (!animation) {
            categoriesState = categories
            return@LaunchedEffect
        }
        animatedScale.animateTo(
            targetValue = 0f,
            animationSpec = tween(100)
        )
        categoriesState = categories
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
        val gap = innerSpacing.toPx()
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

        fun colorOf(i: Int): Color = categoriesState.getOrNull(i)?.color?.toColor() ?: errorColor

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

        when (categoriesState.size.coerceAtMost(4)) {
            0 -> {
                // EMPTY → outline on CONTENT rect so it matches the filled size
                drawRoundRect(
                    color = emptyOutline,
                    style = Stroke(width = ow),
                    topLeft = Offset(contentLeft, contentTop),
                    size = Size(contentW, contentH),
                    cornerRadius = CornerRadius(r, r)
                )
                drawTodayOutline()
            }

            1 -> {
                drawRoundRect(
                    color = colorOf(0),
                    topLeft = Offset(contentLeft, contentTop),
                    size = Size(contentW, contentH),
                    cornerRadius = CornerRadius(r, r)
                )
                drawTodayOutline()
            }

            2 -> {
                clipPath(Path().apply { addRoundRect(contentRoundRect) }) {
                    val tileW = (contentW - gap) / 2f
                    val tileH = contentH
                    // left
                    drawRect(
                        color = colorOf(0),
                        topLeft = Offset(contentLeft, contentTop),
                        size = Size(tileW.coerceAtLeast(0f), tileH)
                    )
                    // right
                    drawRect(
                        color = colorOf(1),
                        topLeft = Offset(contentRight - tileW, contentTop),
                        size = Size(tileW.coerceAtLeast(0f), tileH)
                    )
                }
                drawTodayOutline()
            }

            3 -> {
                clipPath(Path().apply { addRoundRect(contentRoundRect) }) {
                    val rowH = (contentH - gap) / 2f
                    val colW = (contentW - gap) / 2f
                    // TL
                    drawRect(
                        color = colorOf(0),
                        topLeft = Offset(contentLeft, contentTop),
                        size = Size(colW.coerceAtLeast(0f), rowH.coerceAtLeast(0f))
                    )
                    // TR
                    drawRect(
                        color = colorOf(1),
                        topLeft = Offset(contentRight - colW, contentTop),
                        size = Size(colW.coerceAtLeast(0f), rowH.coerceAtLeast(0f))
                    )
                    // bottom full
                    drawRect(
                        color = colorOf(2),
                        topLeft = Offset(contentLeft, contentBottom - rowH),
                        size = Size(contentW, rowH.coerceAtLeast(0f))
                    )
                }
                drawTodayOutline()
            }

            else -> {
                clipPath(Path().apply { addRoundRect(contentRoundRect) }) {
                    val tileW = (contentW - gap) / 2f
                    val tileH = (contentH - gap) / 2f
                    // TL
                    drawRect(
                        color = colorOf(0),
                        topLeft = Offset(contentLeft, contentTop),
                        size = Size(tileW.coerceAtLeast(0f), tileH.coerceAtLeast(0f))
                    )
                    // TR
                    drawRect(
                        color = colorOf(1),
                        topLeft = Offset(contentRight - tileW, contentTop),
                        size = Size(tileW.coerceAtLeast(0f), tileH.coerceAtLeast(0f))
                    )
                    // BL
                    drawRect(
                        color = colorOf(2),
                        topLeft = Offset(contentLeft, contentBottom - tileH),
                        size = Size(tileW.coerceAtLeast(0f), tileH.coerceAtLeast(0f))
                    )
                    // BR
                    drawRect(
                        color = colorOf(3),
                        topLeft = Offset(contentRight - tileW, contentBottom - tileH),
                        size = Size(tileW.coerceAtLeast(0f), tileH.coerceAtLeast(0f))
                    )
                }
                drawTodayOutline()
            }
        }
    }
}
