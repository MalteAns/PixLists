package de.malteans.pixlists.lists.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.core.presentation.util.getAbsoluteMap
import de.malteans.pixlists.core.presentation.util.toRelativeMap
import kotlinx.coroutines.flow.*

class ListStatsViewModel(
    listId: Long,
    repository: PixRepository,
): ViewModel() {

    private val _curPixList = repository.getCurrentPixList(listId)

    private val _selectedCategoryIds: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())

    private val _state = MutableStateFlow(ListStatsState())

    val state = combine(
        _state, _curPixList, _selectedCategoryIds
    ) { state, curPixList, selectedCategoryIds ->
        val allCategories = curPixList?.categories ?: emptyList()
        val selectedCategories = allCategories.filter { selectedCategoryIds.contains(it.id) }
        val absoluteMap = curPixList?.entries?.getAbsoluteMap(selectedCategories, state.dateRange) ?: emptyMap()
        val relativeMap = absoluteMap.toRelativeMap()
        state.copy(
            allCategories = allCategories,
            selectedCategories = selectedCategories,
            absoluteMap = absoluteMap,
            relativeMap = relativeMap,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListStatsState(),
        )

    fun onAction(action: ListStatsAction) {
        when (action) {
            is ListStatsAction.SetCategorySelected -> {
                _selectedCategoryIds.update {
                    if (action.selected) it + action.categoryId
                    else it - action.categoryId
                }
            }
            is ListStatsAction.SetAllCategoriesSelected -> {
                _selectedCategoryIds.update {
                    if (action.selected) state.value.allCategories.map { it.id }.toSet()
                    else emptySet()
                }
            }

            is ListStatsAction.SetDateRange -> {
                _state.update { it.copy(
                    dateRange = action.startDate to action.endDate
                ) }
            }

            else -> throw NotImplementedError("Action '$action' is not implemented in ListStatsViewModel")
        }
    }
}