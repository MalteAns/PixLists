package de.malteans.pixlists.lists.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.lists.presentation.components.ListStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ListViewModel(
    private val repository: PixRepository,
): ViewModel() {

    private val _curPixListId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _curPixList: Flow<PixList?> = _curPixListId
        .flatMapLatest { id ->
            if (id != null) repository.getCurrentPixList(id)
            else flowOf(null)
        }

    private val _state = MutableStateFlow(ListState())

    val state = combine(
        _state,
        _curPixListId,
        _curPixList,
    ) { state, curPixListId, curPixList ->
        ListState(
            curPixList = curPixList,
            listStatus = when {
                curPixListId == null -> ListStatus.EMPTY
                curPixList == null -> ListStatus.LOADING
                else -> ListStatus.OPENED
            },
            curCategories = curPixList?.categories ?: emptyList(),
            colorList = state.colorList,
            invalideNames = state.invalideNames.filter { it != curPixList?.name },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListState()
    )

    fun onAction(action: ListAction) {
        when (action) {
            is ListAction.SetListStatus -> _state.update { it.copy(listStatus = action.status) }

            is ListAction.SetPixListId -> setPixListId(action.pixListId)
            is ListAction.UpdatePixListName -> updatePixListName(action.newName)

            is ListAction.CreatePixCategory -> createPixCategory(action.name, action.color)
            is ListAction.UpdatePixCategory -> updatePixCategory(action.category, action.newName, action.newColor)
            is ListAction.DeletePixCategory -> deletePixCategory(action.category)
            is ListAction.UpdatePixCategoryOrder -> updateCategoryOrder(action.categories)

            is ListAction.SetPixEntry -> setPixEntry(action.date, action.category)

            else -> throw NotImplementedError("Action ${action::class.simpleName} is not implemented in ViewModel")
        }
    }

    private fun setPixListId(pixListId: Long?) {
        _curPixListId.update { pixListId }

        viewModelScope.launch(Dispatchers.IO) {
            _state.update{ state -> state.copy(
                invalideNames = repository.getAllPixListsWithoutData().first()
                    .map { it.name },
                colorList = repository.getAllColors().first()
            ) }
        }
    }

    private fun updatePixListName(newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.renameList(_curPixListId.value!!, newName)
            _state.value = _state.value.copy(
                invalideNames = repository.getAllPixListsWithoutData().first().map { it.name },
            )
        }
    }

    // PixCategory functions -------------------------------------------------
    private fun createPixCategory(name: String, color: PixColor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createCategory(_curPixListId.value!!, color.id, name)
        }
    }

    private fun updatePixCategory(category: PixCategory, newName: String?, newColor: PixColor?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newName != null) {
                repository.renameCategory(category.id, newName)
            }
            if (newColor != null) {
                repository.changeCategoryColor(category.id, newColor.id)
            }
        }
    }

    private fun deletePixCategory(category: PixCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategoryById(category.id)
        }
    }

    // PixEntry functions ---------------------------------------------------
    private fun setPixEntry(date: LocalDate, categories: List<PixCategory>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (categories.size) {
                0 -> repository.deleteEntry(_curPixListId.value!!, date)
                else -> repository.setEntry(_curPixListId.value!!, categories.map { it.id }, date)
            }
        }
    }

    // Update Category Order ------------------------------------------------
    private fun updateCategoryOrder(newOrder: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeCategoriesOrder(_curPixListId.value!!, newOrder)
        }
    }
}