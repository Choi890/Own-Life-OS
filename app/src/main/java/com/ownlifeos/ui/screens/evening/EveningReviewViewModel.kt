package com.ownlifeos.ui.screens.evening

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.automation.EveningReviewDraft
import com.ownlifeos.domain.automation.ReviewDraftGenerator
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EveningReviewUiState(
    val date: String = DateUtils.todayKey(),
    val goodThings: String = "",
    val errorLogs: String = "",
    val carryOver: String = "",
    val draft: EveningReviewDraft = ReviewDraftGenerator.generate(emptyList()),
    val saved: Boolean = false
)

class EveningReviewViewModel(
    private val reviewRepository: ReviewRepository,
    taskRepository: TaskRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private var loadedExisting = false

    val pendingTasks: StateFlow<List<DailyTask>> = taskRepository.observeByDate(date)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(EveningReviewUiState(date = date))
    val uiState: StateFlow<EveningReviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            reviewRepository.observeByDate(date).collect { review ->
                if (review != null && !loadedExisting) {
                    loadedExisting = true
                    _uiState.update {
                        it.copy(
                            goodThings = review.goodThings,
                            errorLogs = review.errorLogs,
                            carryOver = review.carryOver,
                            saved = true
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            pendingTasks.collect { tasks ->
                _uiState.update { it.copy(draft = ReviewDraftGenerator.generate(tasks)) }
            }
        }
    }

    fun updateGoodThings(value: String) {
        _uiState.update { it.copy(goodThings = value, saved = false) }
    }

    fun updateErrorLogs(value: String) {
        _uiState.update { it.copy(errorLogs = value, saved = false) }
    }

    fun updateCarryOver(value: String) {
        _uiState.update { it.copy(carryOver = value, saved = false) }
    }

    fun usePendingTasksAsCarryOver() {
        val carryOver = pendingTasks.value
            .filter { it.status != TaskStatus.DONE }
            .joinToString(separator = "\n") { "- ${it.title}" }

        _uiState.update { it.copy(carryOver = carryOver, saved = false) }
    }

    fun applyDraft() {
        val draft = _uiState.value.draft
        _uiState.update {
            it.copy(
                goodThings = draft.goodThings,
                errorLogs = draft.errorLogs,
                carryOver = draft.carryOver,
                saved = false
            )
        }
    }

    fun save() {
        val current = _uiState.value
        viewModelScope.launch {
            reviewRepository.save(
                date = current.date,
                goodThings = current.goodThings,
                errorLogs = current.errorLogs,
                carryOver = current.carryOver
            )
            loadedExisting = true
            _uiState.update { it.copy(saved = true) }
        }
    }
}
