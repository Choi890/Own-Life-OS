package com.ownlifeos.ui.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.SystemHealthRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.model.SystemHealthReport
import com.ownlifeos.domain.usecase.GetSystemHealthReportV3UseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SystemHealthUiState(
    val report: SystemHealthReport? = null
)

class SystemHealthViewModel(
    private val systemHealthRepository: SystemHealthRepository,
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val endDate = DateUtils.todayKey()
    private val startDate = DateUtils.daysBefore(endDate, 6)
    private val healthUseCase = GetSystemHealthReportV3UseCase()

    val uiState: StateFlow<SystemHealthUiState> = combine(
        checkInRepository.observeRange(startDate, endDate),
        taskRepository.observeRange(startDate, endDate),
        reviewRepository.observeRange(startDate, endDate)
    ) { checkIns, tasks, reviews ->
        val report = healthUseCase.execute(endDate, checkIns, tasks, reviews)
        viewModelScope.launch { systemHealthRepository.save(report) }
        SystemHealthUiState(report = report)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SystemHealthUiState()
    )
}
