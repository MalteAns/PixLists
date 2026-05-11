package de.malteans.pixlists.lists.presentation.view

import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.lists.presentation.view.components.ListStatus
import kotlinx.datetime.LocalDate

sealed interface ListViewAction {
    data object OpenDrawer : ListViewAction
    data object OpenStats : ListViewAction
    data class SetListStatus(val status: ListStatus) : ListViewAction

    data class SetPixListId(val pixListId: Long?) : ListViewAction
    data class UpdatePixListName(val newName: String) : ListViewAction

    data class OnAddCurrentYear(val year: Int) : ListViewAction
    data class OnYearSelected(val yearIndex: Int) : ListViewAction

    data class CreatePixCategory(
        val name: String,
        val color: PixColor,
        val enableWeight: Boolean,
        val minWeight: Int,
        val maxWeight: Int,
        val weightStep: Int,
    ) : ListViewAction
    data class UpdatePixCategory(
        val category: PixCategory,
    ) : ListViewAction
    data class DeletePixCategory(val categoryId: Long) : ListViewAction
    data class UpdatePixCategoryOrder(val newOrder: List<Long>) : ListViewAction

    data class SetPixEntry(
        val date: LocalDate,
        val entries: List<PixEntry>
    ) : ListViewAction
}