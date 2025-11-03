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
            is ManageColorsAction.DeleteUnusedColors -> deleteUnusedColors()
            is ManageColorsAction.AddColor -> addColor(action.name, action.red, action.green, action.blue)
            is ManageColorsAction.AddColors -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.createColors(action.colors)
                }
            }
            is ManageColorsAction.UpdateColor -> updateColor(action.colorToEdit, action.newName, action.newRgb)
            is ManageColorsAction.DeleteColor -> deleteColor(action.colorToDelete)
            else -> throw NotImplementedError("Action not implemented in ViewModel: $action")
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