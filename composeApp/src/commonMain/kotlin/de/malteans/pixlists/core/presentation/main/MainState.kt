package de.malteans.pixlists.core.presentation.main

import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.presentation.main.components.CurScreen

data class MainState(
    val allPixLists: List<PixList> = emptyList(),
    val curPixListId: Long? = null,
    val curScreen: CurScreen = CurScreen.LIST,
)
