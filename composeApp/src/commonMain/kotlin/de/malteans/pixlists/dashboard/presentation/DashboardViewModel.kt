package de.malteans.pixlists.dashboard.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.pixlists.core.domain.PixRepository
import de.malteans.pixlists.core.presentation.components.SnackbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.entry_added_successfully
import pixlists.composeapp.generated.resources.show_list
import kotlin.time.Clock

private val hiddenListRegex = Regex("^\\(.*\\)$")

class DashboardViewModel(
    private val repository: PixRepository,
): ViewModel() {

    private val _pixLists = repository.getAllPixLists()

    private val _widgets = repository.getAllWidgets()

    private val _eventChannel = Channel<DashboardEvent>()
    val events = _eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(DashboardState())

    val state = combine(
        _state,
        _pixLists,
        _widgets,
    ) { state, pixLists, widgets ->
        state.copy(
            pixLists = pixLists.filterNot { it.name.matches(hiddenListRegex) },
            widgets = widgets,
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            DashboardState()
        )

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.AddWidget -> viewModelScope.launch(Dispatchers.IO) {
                repository.createWidget(action.pixListId, action.type, action.categoryIds)
            }
            is DashboardAction.EditWidget -> viewModelScope.launch(Dispatchers.IO) {
                repository.updateWidget(action.widgetId, action.pixListId, action.type, action.categoryIds)
            }
            is DashboardAction.DeleteWidget -> viewModelScope.launch(Dispatchers.IO) {
                repository.deleteWidget(action.widgetId)
            }

            is DashboardAction.AddTodayEntry -> viewModelScope.launch(Dispatchers.IO) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val currentEntry = state.value.pixLists
                    .find { it.id == action.pixListId }
                    ?.entries[today]
                    ?: emptyList()
                repository.setEntry(
                    listId = action.pixListId,
                    categoryIds = currentEntry.map { it.id } + action.categoryId,
                    date = today,
                )
                SnackbarManager.showSnackbar(
                    message = Res.string.entry_added_successfully,
                    actionLabel = Res.string.show_list,
                    duration = SnackbarDuration.Short,
                    onAction = { sendEvent(DashboardEvent.OnOpenList(action.pixListId)) },
                )
            }

            else -> throw NotImplementedError("Action '$action' not implemented in DashboardViewModel")
        }
    }

    fun sendEvent(event: DashboardEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}