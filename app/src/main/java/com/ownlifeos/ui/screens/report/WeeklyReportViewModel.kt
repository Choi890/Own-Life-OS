package com.ownlifeos.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.model.WeeklySystemReport
import com.ownlifeos.domain.usecase.GetWeeklySystemReportUseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class WeeklyReportUiState(
    val report: WeeklySystemReport? = null
)

class WeeklyReportViewModel(
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val endDate = DateUtils.todayKey()
    private val startDate = DateUtils.daysBefore(endDate, 6)
    private val reportUseCase = GetWeeklySystemReportUseCase()

    val uiState: StateFlow<WeeklyReportUiState> = combine(
        checkInRepository.observeRange(startDate, endDate),
        taskRepository.observeRange(startDate, endDate),
        reviewRepository.observeRange(startDate, endDate)
    ) { checkIns, tasks, reviews ->
        WeeklyReportUiState(
            report = reportUseCase.execute(
                endDate = endDate,
                checkIns = checkIns,
                tasks = tasks,
                reviews = reviews
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeeklyReportUiState()
    )
}
