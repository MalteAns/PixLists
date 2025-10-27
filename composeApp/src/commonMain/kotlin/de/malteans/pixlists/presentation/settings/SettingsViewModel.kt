package de.malteans.pixlists.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.domain.PixRepository
import kotlinx.coroutines.Dispatchers
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
                    exportInProgress = true,
                ) }
                viewModelScope.launch(Dispatchers.Default) {
                    val exportData = repository.exportAllData()
                    _state.update { it.copy(
                        exportInProgress = false,
                        exportData = exportData
                    ) }
                }
            }
            is SettingsAction.ResetExportData -> _state.update { it.copy(exportData = null) }
            is SettingsAction.OnImportData -> {
                _state.update { it.copy(
                    importInProgress = true,
                ) }
                viewModelScope.launch(Dispatchers.Default) {
                    repository.importAllData(action.data)
                        .onSuccess {
                            _state.update { it.copy(
                                importSuccess = true,
                                importError = null,
                                importInProgress = false,
                            ) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(
                                importError = error.message ?: "Unknown error",
                                importSuccess = false,
                                importInProgress = false,
                            ) }
                        }
                }
            }
            is SettingsAction.ClearImportFeedback -> _state.update { it.copy(importError = null, importSuccess = false) }
            else -> throw NotImplementedError("Action $action not implemented in ViewModel")
        }
    }
}