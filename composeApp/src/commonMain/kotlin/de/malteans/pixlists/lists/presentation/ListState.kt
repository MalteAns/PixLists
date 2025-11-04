package de.malteans.pixlists.lists.presentation

import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.lists.presentation.components.ListStatus

data class ListState(
    val colorList: List<PixColor> = emptyList(),
    val invalideNames: List<String> = emptyList(),

    val listStatus: ListStatus = ListStatus.LOADING,

    val curPixList: PixList? = null,
    val curCategories: List<PixCategory> = emptyList(),

    val selectedYearIndex: Int = 0,
    val possibleYears: List<Int> = emptyList(),
)
