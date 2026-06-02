package com.ownlifeos.ui.screens.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.LifeSimulationRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.LifeSimulationInput
import com.ownlifeos.domain.model.LifeSimulationResult
import com.ownlifeos.domain.usecase.GetTodaySystemAnalysisUseCase
import com.ownlifeos.domain.usecase.RunLifeSimulationUseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LifeSimulationFormState(
    val title: String = "",
    val description: String = "",
    val expectedEnergyCost: Int = 3,
    val expectedDurationMinutes: Int = 60,
    val urgency: Int = 3,
    val reversibility: Int = 3,
    val importance: Int = 3,
    val resultRequested: Boolean = false,
    val savedMessage: String? = null
)

data class LifeSimulationUiState(
    val form: LifeSimulationFormState = LifeSimulationFormState(),
    val result: LifeSimulationResult? = null
)

class LifeSimulationViewModel(
    private val simulationRepository: LifeSimulationRepository,
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 3)
    private val formState = MutableStateFlow(LifeSimulationFormState())
    private val analysisUseCase = GetTodaySystemAnalysisUseCase()
    private val simulationUseCase = RunLifeSimulationUseCase()

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

    val uiState: StateFlow<LifeSimulationUiState> = combine(
        formState,
        todayFlow,
        recentFlow
    ) { form, today, recent ->
        val result = if (form.title.isBlank() || !form.resultRequested) {
            null
        } else {
            val analysis = analysisUseCase.execute(
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
            simulationUseCase.execute(
                date = date,
                input = form.toInput(),
                analysis = analysis
            )
        }
        LifeSimulationUiState(form = form, result = result)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LifeSimulationUiState()
    )

    fun updateTitle(value: String) = formState.update { it.copy(title = value, resultRequested = false, savedMessage = null) }
    fun updateDescription(value: String) = formState.update { it.copy(description = value, resultRequested = false, savedMessage = null) }
    fun updateEnergy(value: Int) = formState.update {
        val energy = value.coerceIn(1, 5)
        it.copy(
            expectedEnergyCost = energy,
            expectedDurationMinutes = when {
                energy <= 2 -> 15
                energy >= 4 -> 120
                else -> 60
            },
            importance = when {
                energy <= 2 -> 2
                energy >= 4 -> 4
                else -> 3
            },
            resultRequested = false,
            savedMessage = null
        )
    }
    fun updateDuration(value: String) = formState.update {
        it.copy(
            expectedDurationMinutes = value.toIntOrNull()?.coerceIn(5, 480) ?: it.expectedDurationMinutes,
            resultRequested = false,
            savedMessage = null
        )
    }
    fun updateUrgency(value: Int) = formState.update { it.copy(urgency = value.coerceIn(1, 5), resultRequested = false, savedMessage = null) }
    fun updateReversibility(value: Int) = formState.update {
        it.copy(reversibility = value.coerceIn(1, 5), resultRequested = false, savedMessage = null)
    }
    fun updateImportance(value: Int) = formState.update { it.copy(importance = value.coerceIn(1, 5), resultRequested = false, savedMessage = null) }

    fun checkSimulation() {
        formState.update {
            if (it.title.isBlank()) it else it.copy(resultRequested = true, savedMessage = null)
        }
    }

    fun saveSimulation() {
        val state = uiState.value
        val result = state.result ?: return
        viewModelScope.launch {
            simulationRepository.save(result, state.form.description)
            formState.update { it.copy(savedMessage = "시뮬레이션을 저장했습니다.") }
        }
    }

    private fun LifeSimulationFormState.toInput(): LifeSimulationInput = LifeSimulationInput(
        title = title,
        description = description,
        expectedEnergyCost = expectedEnergyCost,
        expectedDurationMinutes = expectedDurationMinutes,
        urgency = urgency,
        reversibility = reversibility,
        importance = importance
    )
}
