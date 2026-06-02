package com.ownlifeos.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.ForecastRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.RebalancedDayPlan
import com.ownlifeos.domain.model.RecoveryPlan
import com.ownlifeos.domain.usecase.GetFutureLoadForecastUseCase
import com.ownlifeos.domain.usecase.GetRebalancedDayPlanUseCase
import com.ownlifeos.domain.usecase.GetRecoveryPlanUseCase
import com.ownlifeos.domain.usecase.GetTodaySystemAnalysisUseCase
import com.ownlifeos.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ForecastUiState(
    val forecast: FutureLoadForecast? = null,
    val rebalancedPlan: RebalancedDayPlan? = null,
    val recoveryPlan: RecoveryPlan? = null
)

class ForecastViewModel(
    private val forecastRepository: ForecastRepository,
    checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 3)
    private val analysisUseCase = GetTodaySystemAnalysisUseCase()
    private val forecastUseCase = GetFutureLoadForecastUseCase()
    private val rebalanceUseCase = GetRebalancedDayPlanUseCase()
    private val recoveryUseCase = GetRecoveryPlanUseCase()

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

    val uiState: StateFlow<ForecastUiState> = combine(todayFlow, recentFlow) { today, recent ->
        val inputs = AnalysisInputs(
            date = date,
            todayCheckIn = today.first,
            todayTasks = today.second,
            todayReview = today.third,
            recentCheckIns = recent.first,
            recentTasks = recent.second,
            recentReviews = recent.third
        )
        val analysis = analysisUseCase.execute(inputs)
        val forecast = forecastUseCase.execute(inputs, analysis)
        viewModelScope.launch { forecastRepository.replaceForDate(forecast.results) }
        ForecastUiState(
            forecast = forecast,
            rebalancedPlan = rebalanceUseCase.execute(date, today.second, analysis),
            recoveryPlan = recoveryUseCase.execute(inputs, analysis, forecast)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ForecastUiState()
    )
}
