package de.malteans.pixlists.presentation.main

import de.malteans.pixlists.domain.PixList
import de.malteans.pixlists.presentation.main.components.CurScreen

data class MainState(
    val allPixLists: List<PixList> = emptyList(),
    val curPixListId: Long? = null,
    val curScreen: CurScreen = CurScreen.LIST,
)
