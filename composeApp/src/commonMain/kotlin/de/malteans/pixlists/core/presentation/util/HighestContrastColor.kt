package de.malteans.pixlists.core.presentation.util

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.pow

/** sRGB -> linear */
private fun lin(c: Float): Float =
    if (c <= 0.03928f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)

/** Relative luminance per WCAG */
private fun luminance(color: Color): Float {
    val r = lin(color.red)
    val g = lin(color.green)
    val b = lin(color.blue)
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

/** Contrast ratio per WCAG */
private fun contrastRatio(a: Color, b: Color): Float {
    val l1 = luminance(a)
    val l2 = luminance(b)
    val hi = max(l1, l2)
    val lo = if (hi == l1) l2 else l1
    return (hi + 0.05f) / (lo + 0.05f)
}

/**
 * Calculates the color from [textColors] that has the highest contrast ratio against the [background] color.
 *
 * @param[background] the background color to contrast against
 * @param[textColors] list of colors to choose the best contrasting color from
 * @return best contrasting color from [textColors] against the [background] color
 */
fun highestContrastColor(
    background: Color,
    textColors: List<Color>,
): Color {
    var bestContrast = contrastRatio(background, background)
    var bestColor = background

    textColors.forEach { color ->
        val contrast = contrastRatio(background, color)
        if (contrast > bestContrast) {
            bestContrast = contrast
            bestColor = color
        }
    }

    return bestColor
}
