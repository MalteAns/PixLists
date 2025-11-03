package de.malteans.pixlists.colors.presentation

import de.malteans.pixlists.core.domain.PixColor

data class ManageColorsState(
    val colorList: List<PixColor> = emptyList(),
    val colorUses: Map<Long, Int> = emptyMap(),
)
