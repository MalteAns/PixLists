package de.malteans.pixlists.presentation.list

import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.domain.PixColor
import de.malteans.pixlists.presentation.list.components.ListStatus
import kotlinx.datetime.LocalDate

sealed interface ListAction {
    data object OpenDrawer : ListAction
    data class SetListStatus(val status: ListStatus) : ListAction

    data class SetPixListId(val pixListId: Long?) : ListAction
    data class UpdatePixListName(val newName: String) : ListAction

    data class CreatePixCategory(
        val name: String,
        val color: PixColor
    ) : ListAction
    data class UpdatePixCategory(
        val category: PixCategory,
        val newName: String?,
        val newColor: PixColor?
    ) : ListAction
    data class DeletePixCategory(val category: PixCategory) : ListAction
    data class UpdatePixCategoryOrder(val categories: List<Long>) : ListAction

    data class SetPixEntry(
        val date: LocalDate,
        val category: PixCategory?
    ) : ListAction
}