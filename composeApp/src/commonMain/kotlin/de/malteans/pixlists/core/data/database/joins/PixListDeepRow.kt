package de.malteans.pixlists.core.data.database.joins

import kotlinx.datetime.LocalDate

data class PixListDeepRow(
    val listId: Long,
    val listName: String,
    val listYearsJson: String,

    val categoryId: Long?,         // null if the list has no categories yet
    val categoryName: String?,
    val categoryEnableWeight: Boolean?,
    val categoryMinWeight: Int?,
    val categoryMaxWeight: Int?,
    val categoryWeightStep: Int?,
    val categoryOrderIndex: Int?,

    val colorId: Long?,            // null if color is null or was deleted (SET NULL)
    val colorName: String?,
    val colorRed: Float?,
    val colorGreen: Float?,
    val colorBlue: Float?,

    val entryId: Long?,            // null if there’s no entry for that row
    val entryDate: LocalDate?,
    val entryWeight: Int?          // null if weight is empty
)
