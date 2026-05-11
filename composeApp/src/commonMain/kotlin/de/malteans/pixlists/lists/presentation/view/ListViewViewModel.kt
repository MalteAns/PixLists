package de.malteans.pixlists.lists.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.lists.presentation.view.components.ListStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewViewModel(
    private val repository: PixRepository,
): ViewModel() {

    private val _curPixListId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _curPixList: Flow<PixList?> = _curPixListId
        .flatMapLatest { id ->
            if (id != null) repository.getCurrentPixList(id)
            else flowOf(null)
        }

    private val _state = MutableStateFlow(ListViewState())

    val state = combine(
        _state,
        _curPixListId,
        _curPixList,
    ) { state, curPixListId, curPixList ->
        state.copy(
            colorList = state.colorList,
            invalideNames = state.invalideNames.filter { it != curPixList?.name },

            listStatus = when {
                curPixListId == null -> ListStatus.EMPTY
                curPixList == null -> ListStatus.LOADING
                else -> ListStatus.OPENED
            },

            curPixList = curPixList,
            curCategories = curPixList?.categories ?: emptyList(),

            possibleYears = curPixList?.years ?: emptyList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListViewState()
    )

    fun onAction(action: ListViewAction) {
        when (action) {
            is ListViewAction.SetListStatus -> _state.update { it.copy(listStatus = action.status) }

            is ListViewAction.SetPixListId -> {
                _curPixListId.update { action.pixListId }

                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { state -> state.copy(
                        invalideNames = repository.getAllPixListsWithoutData().first()
                            .map { it.name },
                        colorList = repository.getAllColors().first()
                    ) }
                }
            }
            is ListViewAction.UpdatePixListName -> viewModelScope.launch(Dispatchers.IO) {
                repository.renameList(_curPixListId.value!!, action.newName)
                _state.update { it.copy(
                    invalideNames = repository.getAllPixListsWithoutData().first().map { it.name },
                ) }
            }

            is ListViewAction.OnAddCurrentYear -> {
                _curPixListId.value?.let { listId ->
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.addYearToList(listId, action.year)
                    }
                }
            }
            is ListViewAction.OnYearSelected -> _state.update { it.copy(
                selectedYearIndex = action.yearIndex,
            ) }

            is ListViewAction.CreatePixCategory -> viewModelScope.launch(Dispatchers.IO) {
                repository.createCategory(
                    _curPixListId.value!!, action.color.id, action.name,
                    action.enableWeight, action.minWeight, action.maxWeight, action.weightStep
                )
            }
            is ListViewAction.UpdatePixCategory -> viewModelScope.launch(Dispatchers.IO) {
                repository.updateCategory(action.category)
            }
            is ListViewAction.DeletePixCategory -> viewModelScope.launch(Dispatchers.IO) {
                repository.deleteCategoryById(action.categoryId)
            }
            is ListViewAction.UpdatePixCategoryOrder -> viewModelScope.launch(Dispatchers.IO) {
                repository.changeCategoriesOrder(_curPixListId.value!!, action.newOrder)
            }

            is ListViewAction.SetPixEntry -> viewModelScope.launch(Dispatchers.IO) {
                when (action.entries.size) {
                    0 -> repository.deleteEntry(_curPixListId.value!!, action.date)
                    else -> repository.setEntry(_curPixListId.value!!, action.entries, action.date)
                }
            }

            else -> throw NotImplementedError("Action $action is not implemented in ViewModel")
        }
    }
}
