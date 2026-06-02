package com.ownlifeos.ui.screens.decision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.DecisionRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.DecisionPrediction
import com.ownlifeos.domain.model.DecisionRecord
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.usecase.EvaluateDecisionUseCase
import com.ownlifeos.domain.usecase.GetTodaySystemAnalysisUseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DecisionFormState(
    val title: String = "",
    val description: String = "",
    val expectedEnergyCost: Int = 3,
    val urgency: Int = 3,
    val reversibility: Int = 3,
    val importance: Int = 3,
    val savedMessage: String? = null
)

data class DecisionCheckUiState(
    val form: DecisionFormState = DecisionFormState(),
    val analysis: TodaySystemAnalysis? = null,
    val prediction: DecisionPrediction? = null,
    val recentDecisions: List<DecisionRecord> = emptyList()
)

class DecisionCheckViewModel(
    private val decisionRepository: DecisionRepository,
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 3)
    private val analysisUseCase = GetTodaySystemAnalysisUseCase()
    private val decisionUseCase = EvaluateDecisionUseCase()

    private val formState = MutableStateFlow(DecisionFormState())

    private val todayFlow = combine(
        checkInRepository.observeByDate(date),
        taskRepository.observeByDate(date),
        reviewRepository.observeByDate(date)
    ) { checkIn, tasks, review -> Triple(checkIn, tasks, review) }

    private val recentFlow = combine(
        checkInRepository.observeRange(recentStart, date),
        taskRepository.observeRange(recentStart, date),
        reviewRepository.observeRange(recentStart, date)
    ) { checkIns, tasks, reviews -> Triple(checkIns, tasks, reviews) }

    private val analysisFlow = combine(
        todayFlow,
        recentFlow
    ) { today, recent ->
        analysisUseCase.execute(
            AnalysisInputs(
                date = date,
                todayCheckIn = today.first,
                todayTasks = today.second,
                todayReview = today.third,
                recentCheckIns = recent.first,
                recentTasks = recent.second,
                recentReviews = recent.third
            )
        )
    }

    val uiState: StateFlow<DecisionCheckUiState> = combine(
        formState,
        analysisFlow,
        decisionRepository.observeRecent()
    ) { form, analysis, recentDecisions ->
        val prediction = if (form.title.isBlank()) {
            null
        } else {
            decisionUseCase.execute(
                title = form.title,
                expectedEnergyCost = form.expectedEnergyCost,
                urgency = form.urgency,
                reversibility = form.reversibility,
                importance = form.importance,
                analysis = analysis
            )
        }
        DecisionCheckUiState(
            form = form,
            analysis = analysis,
            prediction = prediction,
            recentDecisions = recentDecisions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DecisionCheckUiState()
    )

    val form: StateFlow<DecisionFormState> = formState.asStateFlow()

    fun updateTitle(value: String) = formState.update { it.copy(title = value, savedMessage = null) }

    fun updateDescription(value: String) = formState.update { it.copy(description = value, savedMessage = null) }

    fun updateEnergy(value: Int) = formState.update { it.copy(expectedEnergyCost = value.coerceIn(1, 5), savedMessage = null) }

    fun updateUrgency(value: Int) = formState.update { it.copy(urgency = value.coerceIn(1, 5), savedMessage = null) }

    fun updateReversibility(value: Int) = formState.update { it.copy(reversibility = value.coerceIn(1, 5), savedMessage = null) }

    fun updateImportance(value: Int) = formState.update { it.copy(importance = value.coerceIn(1, 5), savedMessage = null) }

    fun saveDecision() {
        val state = uiState.value
        val prediction = state.prediction ?: return
        val form = state.form
        viewModelScope.launch {
            decisionRepository.saveDecision(
                date = date,
                title = form.title,
                description = form.description,
                expectedEnergyCost = form.expectedEnergyCost,
                urgency = form.urgency,
                reversibility = form.reversibility,
                importance = form.importance,
                prediction = prediction
            )
            formState.update { it.copy(savedMessage = "선택 기록을 저장했습니다.") }
        }
    }
}
