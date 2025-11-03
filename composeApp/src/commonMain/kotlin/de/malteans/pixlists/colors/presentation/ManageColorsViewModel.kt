package de.malteans.pixlists.colors.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageColorsViewModel(
    private val repository: PixRepository
): ViewModel() {

    private val _colorsWithUses = repository
        .getAllColorsWithUses()

    private val _state = MutableStateFlow(ManageColorsState())

    val state = combine(
        _state, _colorsWithUses
    ) { state, colorsWithUses ->
        state.copy(
            colorList = colorsWithUses.keys.toList().sortedBy { it.name },
            colorUses = colorsWithUses.mapKeys { it.key.id }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ManageColorsState())

    fun onAction(action: ManageColorsAction) {
        when (action) {
            is ManageColorsAction.LoadDefaultColors -> loadDefaultColors()
            is ManageColorsAction.DeleteUnusedColors -> deleteUnusedColors()
            is ManageColorsAction.AddColor -> addColor(action.name, action.red, action.green, action.blue)
            is ManageColorsAction.UpdateColor -> updateColor(action.colorToEdit, action.newName, action.newRgb)
            is ManageColorsAction.DeleteColor -> deleteColor(action.colorToDelete)
            else -> throw NotImplementedError("Action not implemented in ViewModel: $action")
        }
    }

    private fun loadDefaultColors() {
        val invalideNames = state.value.colorList.map { it.name }
        listOf(
            PixColor(name = "Peach", red = 1.0f, green = 0.87f, blue = 0.77f),
            PixColor(name = "Lemon Yellow", red = 1.0f, green = 0.97f, blue = 0.69f),
            PixColor(name = "Mint Green", red = 0.74f, green = 0.98f, blue = 0.79f),
            PixColor(name = "Sky Blue", red = 0.68f, green = 0.85f, blue = 0.90f),
            PixColor(name = "Lavender", red = 0.82f, green = 0.75f, blue = 0.93f),
            PixColor(name = "Dusty Pink", red = 0.91f, green = 0.75f, blue = 0.80f),
            PixColor(name = "Pale Orange", red = 1.0f, green = 0.85f, blue = 0.72f),
            PixColor(name = "Baby Blue", red = 0.68f, green = 0.90f, blue = 1.0f),
            PixColor(name = "Blush Pink", red = 1.0f, green = 0.82f, blue = 0.86f),
            PixColor(name = "Pastel Lilac", red = 0.91f, green = 0.78f, blue = 0.94f)
        ).filter { it.name !in invalideNames }.forEach { color ->
            addColor(color.name, color.red, color.green, color.blue)
        }
    }

    private fun deleteUnusedColors() {
        viewModelScope.launch(Dispatchers.IO) {
            val amount = repository.deleteUnusedColors()
            println(amount)
        }
    }

    // Color DB operations
    private fun addColor(name: String, red: Float, green: Float, blue: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createColor(name, red, green, blue)
        }
    }

    private fun updateColor(colorToEdit: PixColor, newName: String?, newRgb: List<Float>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newName != null) {
                repository.renameColor(colorToEdit.id, newName)
            }
            if (newRgb != null) {
                repository.changeColor(colorToEdit.id, newRgb[0], newRgb[1], newRgb[2])
            }
        }
    }

    private fun deleteColor(color: PixColor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteColorById(color.id)
        }
    }
}