package de.malteans.pixlists.lists.presentation.view

import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
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
        val color: PixColor
    ) : ListViewAction
    data class UpdatePixCategory(
        val category: PixCategory,
        val newName: String?,
        val newColor: PixColor?
    ) : ListViewAction
    data class DeletePixCategory(val category: PixCategory) : ListViewAction
    data class UpdatePixCategoryOrder(val categories: List<Long>) : ListViewAction

    data class SetPixEntry(
        val date: LocalDate,
        val category: List<PixCategory>
    ) : ListViewAction
}