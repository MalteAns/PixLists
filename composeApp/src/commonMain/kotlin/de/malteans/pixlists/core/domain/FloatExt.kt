package de.malteans.pixlists.core.domain

import kotlin.math.absoluteValue

fun Float.toHex(): String {
    val hex = (this * 255).toInt().absoluteValue.coerceAtMost(255).toString(16)
    return if (hex.length == 1) "0$hex" else hex
}
