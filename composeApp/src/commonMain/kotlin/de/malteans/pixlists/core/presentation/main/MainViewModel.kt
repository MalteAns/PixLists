package de.malteans.pixlists.core.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.DataStoreRepository
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.core.presentation.main.components.CurScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: PixRepository,
    private val dataStoreRepository: DataStoreRepository,
): ViewModel() {

    private val _allPixLists = repository
        .getAllPixListsWithoutData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _showStartColorDialog = dataStoreRepository
        .getShowStartColorDialogFlow()

    private val _state = MutableStateFlow(MainState())

    val state = combine(
        _state,
        _allPixLists,
        _showStartColorDialog
    ) { state, allPixLists, showStartColorDialog ->
        state.copy(
            allPixLists = allPixLists,
            showStartColorDialog = showStartColorDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainState()
    )

    suspend fun createPixList(name: String): Long {
        return repository.createList(name)
    }

    fun deletePixListById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteListById(id)
        }
    }

    fun setCurPixListId(id: Long?) {
        _state.value = _state.value.copy(curPixListId = id)
    }

    fun setCurScreen(screen: CurScreen) {
        _state.value = _state.value.copy(curScreen = screen)
    }

    fun dismissStartColorDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setShowStartColorDialog(false)
        }
    }

    fun addStartColors(colors: List<PixColor>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createColors(colors)
        }
    }
}