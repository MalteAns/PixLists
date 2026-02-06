package de.malteans.pixlists.lists.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.domain.PixRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ListStatsViewModel(
    private val listId: Long,
    private val repository: PixRepository,
): ViewModel() {

    private lateinit var curPixList: PixList

    private val _state = MutableStateFlow(ListStatsState())

    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListStatsState(),
        )

    init {
        viewModelScope.launch {
            curPixList = repository.getCurrentPixList(listId).first()
                ?: throw IllegalArgumentException("ListId could not be fetched") // TODO: Better error handling
            _state.update { it.copy(
                allCategories = curPixList.categories,
            ) }
            updateStats()
        }
    }

    fun onAction(action: ListStatsAction) {
        when (action) {
            is ListStatsAction.IncludeCategory -> {
                _state.update { it.copy(isLoading = true) }
                updateStats(
                    categories = _state.value.statsMap.keys.toList().let { current ->
                        if (action.include) current + action.category
                        else current - action.category
                    }
                )
            }
            is ListStatsAction.SetDateRange -> {
                _state.update { it.copy(isLoading = true) }
                _state.update { it.copy(dateRange = Pair(action.startDate, action.endDate)) }
                updateStats(
                    dateRange = Pair(action.startDate, action.endDate),
                )
            }

            else -> throw NotImplementedError("Action '$action' is not implemented in ListStatsViewModel")
        }
    }

    private fun updateStats(
        categories: List<PixCategory?> = _state.value.allCategories,
        dateRange: Pair<LocalDate?, LocalDate?>? = _state.value.dateRange,
    ) {
        val entries = curPixList.entries
        val absoluteCountMap = mutableMapOf<PixCategory?, Int>()

        var totalCount = 0
        for ((date, entryCategories) in entries) {
            if (dateRange != null) {
                val (startDate, endDate) = dateRange
                if ((startDate != null && date < startDate) || (endDate != null && date > endDate)) {
                    continue
                }
            }

            for (category in categories) {
                if (entryCategories.contains(category)) {
                    absoluteCountMap[category] = (absoluteCountMap[category] ?: 0) + 1
                    totalCount++
                }
            }
        }

        val statsMap = absoluteCountMap.mapValues { (category, absCount) ->
            val relCount = if (totalCount > 0) absCount.toDouble() / totalCount else 0.0
            Pair(absCount, relCount)
        }

        _state.update { it.copy(
            statsMap = statsMap,
            isLoading = false,
        ) }
    }
}