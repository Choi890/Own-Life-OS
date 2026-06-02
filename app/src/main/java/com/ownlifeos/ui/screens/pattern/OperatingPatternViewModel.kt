package com.ownlifeos.ui.screens.pattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.RecommendationFeedbackRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.analysis.OperatingPatternAnalyzer
import com.ownlifeos.domain.model.OperatingPatternReport
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class OperatingPatternUiState(
    val report: OperatingPatternReport? = null
)

class OperatingPatternViewModel(
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository,
    feedbackRepository: RecommendationFeedbackRepository
) : ViewModel() {
    private val endDate = DateUtils.todayKey()
    private val startDate = DateUtils.daysBefore(endDate, 13)

    val uiState: StateFlow<OperatingPatternUiState> = combine(
        checkInRepository.observeRange(startDate, endDate),
        taskRepository.observeRange(startDate, endDate),
        reviewRepository.observeRange(startDate, endDate),
        feedbackRepository.observeRange(startDate, endDate)
    ) { checkIns, tasks, reviews, feedbacks ->
        OperatingPatternUiState(
            report = OperatingPatternAnalyzer.analyze(
                startDate = startDate,
                endDate = endDate,
                checkIns = checkIns,
                tasks = tasks,
                reviews = reviews,
                feedbacks = feedbacks
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OperatingPatternUiState()
    )
}
