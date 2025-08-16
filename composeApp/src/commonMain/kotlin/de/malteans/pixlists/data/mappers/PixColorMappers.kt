package de.malteans.pixlists.data.mappers

import de.malteans.pixlists.data.database.entities.PixColorEntity
import de.malteans.pixlists.domain.PixColor

fun PixColorEntity.toDomain(): PixColor {
    return PixColor(
        id = id,
        name = name,
        red = red,
        green = green,
        blue = blue,
    )
}