package de.malteans.pixlists.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.domain.PixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    val repository: PixRepository
): ViewModel() {

    private val _state = MutableStateFlow(SettingState())

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnExportData -> {
                _state.update { it.copy(
                    isLoading = true,
                ) }
                viewModelScope.launch {
                    val exportData = repository.exportAllData()
                    _state.update { it.copy(
                        isLoading = false,
                        exportData = exportData
                    ) }
                }
            }
            is SettingsAction.ResetExportData -> _state.update { it.copy(exportData = null) }
            is SettingsAction.OnImportData -> {
                _state.update { it.copy(
                    isLoading = true,
                ) }
                viewModelScope.launch {
                    try {
                        repository.importAllData(action.data)
                    } catch (e: IllegalStateException) {
                        _state.update { it.copy(
                            importError = "Import failed: ${e.message}",
                        ) }
                    } finally {
                        _state.update { it.copy(
                            isLoading = false,
                        ) }
                    }
                }
            }
            is SettingsAction.ClearImportError -> _state.update { it.copy(importError = null) }
            else -> throw NotImplementedError("Action $action not implemented in ViewModel")
        }
    }
}