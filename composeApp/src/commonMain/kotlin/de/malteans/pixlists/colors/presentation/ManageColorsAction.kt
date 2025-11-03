package de.malteans.pixlists.colors.presentation

import de.malteans.pixlists.core.domain.PixColor

sealed interface ManageColorsAction {
    data object OpenDrawer : ManageColorsAction

    data object LoadDefaultColors : ManageColorsAction
    data object DeleteUnusedColors : ManageColorsAction
    data class AddColor(val name: String, val red: Float, val green: Float, val blue: Float) : ManageColorsAction
    data class UpdateColor(val colorToEdit: PixColor, val newName: String?, val newRgb: List<Float>?) : ManageColorsAction
    data class DeleteColor(val colorToDelete: PixColor) : ManageColorsAction
}