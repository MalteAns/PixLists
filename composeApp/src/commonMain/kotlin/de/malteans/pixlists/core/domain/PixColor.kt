package de.malteans.pixlists.core.domain

import androidx.compose.ui.graphics.Color

data class PixColor(
    val id: Long = 0L,
    val name: String,
    val red: Float,
    val green: Float,
    val blue: Float,
) {
    fun toColor(): Color {
        return Color(red = red, green = green, blue = blue)
    }

    fun toHex(): String {
        return "#${red.toHex()}${green.toHex()}${blue.toHex()}"
    }

    fun getRgbValues(): List<Float> {
        return listOf(red, green, blue)
    }
}
