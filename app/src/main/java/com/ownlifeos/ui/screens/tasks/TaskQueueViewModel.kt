package com.ownlifeos.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.LifeBatteryAnalyzer
import com.ownlifeos.domain.automation.TaskBurdenEstimator
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RankedTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.usecase.GetRankedTaskQueueUseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskFormState(
    val title: String = "",
    val importance: Int = 3,
    val energyCost: Int = 3,
    val focusNeed: Int = 3,
    val deadlineDate: String = "",
    val burdenEstimateLabel: String = "보통",
    val burdenEstimateReason: String = "제목을 입력하면 부담도를 자동으로 다시 추정합니다.",
    val burdenAutoEstimated: Boolean = true,
    val message: String? = null
)

class TaskQueueViewModel(
    private val taskRepository: TaskRepository,
    private val checkInRepository: CheckInRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 3)
    private val rankUseCase = GetRankedTaskQueueUseCase()

    val tasks: StateFlow<List<DailyTask>> = taskRepository.observeByDate(date)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val todayFlow = combine(
        taskRepository.observeByDate(date),
        checkInRepository.observeByDate(date),
        reviewRepository.observeByDate(date)
    ) { tasks, checkIn, review -> Triple(tasks, checkIn, review) }

    private val recentFlow = combine(
        checkInRepository.observeRange(recentStart, date),
        taskRepository.observeRange(recentStart, date),
        reviewRepository.observeRange(recentStart, date)
    ) { checkIns, tasks, reviews -> Triple(checkIns, tasks, reviews) }

    val rankedTasks: StateFlow<List<RankedTask>> = combine(
        todayFlow,
        recentFlow
    ) { today, recent ->
        val todayTasks = today.first
        val inputs = AnalysisInputs(
            date = date,
            todayCheckIn = today.second,
            todayTasks = todayTasks,
            todayReview = today.third,
            recentCheckIns = recent.first,
            recentTasks = recent.second,
            recentReviews = recent.third
        )
        rankUseCase.execute(
            tasks = todayTasks,
            batteryAnalysis = LifeBatteryAnalyzer.analyze(inputs),
            today = date
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    fun updateTitle(value: String) {
        val estimate = TaskBurdenEstimator.estimate(value)
        _formState.update {
            if (it.burdenAutoEstimated) {
                it.copy(
                    title = value,
                    importance = estimate.importance,
                    energyCost = estimate.energyCost,
                    focusNeed = estimate.focusNeed,
                    burdenEstimateLabel = estimate.label,
                    burdenEstimateReason = estimate.reason,
                    message = null
                )
            } else {
                it.copy(
                    title = value,
                    burdenEstimateLabel = estimate.label,
                    burdenEstimateReason = estimate.reason,
                    message = null
                )
            }
        }
    }

    fun updateImportance(value: Int) {
        _formState.update { it.copy(importance = value.coerceIn(1, 5), message = null) }
    }

    fun updateEnergyCost(value: Int) {
        val energyCost = value.coerceIn(1, 5)
        _formState.update {
            it.copy(
                energyCost = energyCost,
                burdenEstimateLabel = TaskBurdenEstimator.labelForEnergy(energyCost),
                burdenEstimateReason = "사용자가 부담도를 직접 선택했습니다.",
                burdenAutoEstimated = false,
                message = null
            )
        }
    }

    fun updateFocusNeed(value: Int) {
        _formState.update { it.copy(focusNeed = value.coerceIn(1, 5), message = null) }
    }

    fun updateDeadlineDate(value: String) {
        _formState.update { it.copy(deadlineDate = value, message = null) }
    }

    fun addTask() {
        val current = _formState.value
        if (current.title.isBlank()) {
            _formState.update { it.copy(message = "할 일을 입력하세요.") }
            return
        }

        viewModelScope.launch {
            taskRepository.addTask(
                date = date,
                title = current.title,
                importance = current.importance,
                energyCost = current.energyCost,
                deadlineDate = current.deadlineDate,
                focusNeed = current.focusNeed
            )
            _formState.value = TaskFormState(message = "오늘 작업으로 추가했습니다.")
        }
    }

    fun updateStatus(taskId: Long, status: TaskStatus) {
        viewModelScope.launch {
            taskRepository.updateStatus(taskId = taskId, status = status)
        }
    }

    fun delete(task: DailyTask) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }
    }

    fun defer(taskId: Long) {
        viewModelScope.launch {
            taskRepository.defer(taskId)
        }
    }
}
