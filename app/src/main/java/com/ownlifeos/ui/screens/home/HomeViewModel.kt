package com.ownlifeos.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ownlifeos.domain.automation.CheckInEstimator
import com.ownlifeos.domain.automation.EstimatedCheckIn
import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.LifeBatteryAnalyzer
import com.ownlifeos.domain.analysis.TaskQueueRanker
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.RecommendationFeedbackRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.LifeMetrics
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.RecommendationSurface
import com.ownlifeos.domain.model.RebalancedDayPlan
import com.ownlifeos.domain.model.RecoveryPlan
import com.ownlifeos.domain.model.SystemHealthReport
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.model.TodayMode
import com.ownlifeos.domain.usecase.GetFutureLoadForecastUseCase
import com.ownlifeos.domain.usecase.GetRebalancedDayPlanUseCase
import com.ownlifeos.domain.usecase.GetRecoveryPlanUseCase
import com.ownlifeos.domain.usecase.GetSystemHealthReportV3UseCase
import com.ownlifeos.domain.usecase.GetTodaySystemAnalysisUseCase
import com.ownlifeos.util.DateUtils
import com.ownlifeos.widget.WidgetSnapshot
import com.ownlifeos.widget.WidgetSnapshotStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val date: String = DateUtils.todayKey(),
    val checkIn: DailyCheckIn? = null,
    val tasks: List<DailyTask> = emptyList(),
    val review: DailyReview? = null,
    val metrics: LifeMetrics = LifeMetrics(),
    val analysis: TodaySystemAnalysis? = null,
    val forecast: FutureLoadForecast? = null,
    val rebalancedPlan: RebalancedDayPlan? = null,
    val recoveryPlan: RecoveryPlan? = null,
    val systemHealthReport: SystemHealthReport? = null,
    val estimatedCheckIn: EstimatedCheckIn? = null,
    val latestStrategyFeedback: RecommendationFeedback? = null
)

class HomeViewModel(
    private val checkInRepository: CheckInRepository,
    taskRepository: TaskRepository,
    reviewRepository: ReviewRepository,
    private val feedbackRepository: RecommendationFeedbackRepository,
    private val widgetSnapshotStore: WidgetSnapshotStore
) : ViewModel() {
    private val date = DateUtils.todayKey()
    private val recentStart = DateUtils.daysBefore(date, 6)
    private val analysisUseCase = GetTodaySystemAnalysisUseCase()
    private val forecastUseCase = GetFutureLoadForecastUseCase()
    private val rebalanceUseCase = GetRebalancedDayPlanUseCase()
    private val recoveryUseCase = GetRecoveryPlanUseCase()
    private val healthUseCase = GetSystemHealthReportV3UseCase()

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

    val uiState: StateFlow<HomeUiState> = combine(
        todayFlow,
        recentFlow,
        feedbackRepository.observeLatestForSurface(date, RecommendationSurface.DAILY_STRATEGY)
    ) { today, recent, latestStrategyFeedback ->
        val checkIn = today.first
        val tasks = today.second
        val review = today.third
        val inputs = AnalysisInputs(
            date = date,
            todayCheckIn = checkIn,
            todayTasks = tasks,
            todayReview = review,
            recentCheckIns = recent.first,
            recentTasks = recent.second,
            recentReviews = recent.third
        )
        val analysis = analysisUseCase.execute(inputs)
        val battery = LifeBatteryAnalyzer.analyze(inputs)
        val rankedTasks = TaskQueueRanker.rank(tasks, battery, date)
        val forecast = forecastUseCase.execute(inputs, analysis)
        val rebalancedPlan = rebalanceUseCase.execute(date, tasks, analysis)
        val recoveryPlan = recoveryUseCase.execute(inputs, analysis, forecast)
        val systemHealthReport = healthUseCase.execute(
            endDate = date,
            checkIns = recent.first,
            tasks = recent.second,
            reviews = recent.third
        )
        val estimatedCheckIn = CheckInEstimator.estimate(
            date = date,
            recentCheckIns = recent.first,
            todayTasks = tasks,
            recentTasks = recent.second,
            recentReviews = recent.third
        )
        widgetSnapshotStore.save(
            buildWidgetSnapshot(
                analysis = analysis,
                forecast = forecast,
                rebalancedPlan = rebalancedPlan,
                recoveryPlan = recoveryPlan
            )
        )

        HomeUiState(
            date = date,
            checkIn = checkIn,
            tasks = tasks,
            review = review,
            analysis = analysis,
            forecast = forecast,
            rebalancedPlan = rebalancedPlan,
            recoveryPlan = recoveryPlan,
            systemHealthReport = systemHealthReport,
            estimatedCheckIn = estimatedCheckIn,
            latestStrategyFeedback = latestStrategyFeedback,
            metrics = LifeMetrics(
                lifeBattery = analysis.lifeBattery,
                focusLevel = analysis.focusLevel,
                stressLevel = analysis.stressLoad,
                totalTasks = tasks.size,
                completedTasks = tasks.count { it.status == TaskStatus.DONE },
                activeEnergyCost = tasks.filter { it.status != TaskStatus.DONE }.sumOf { it.energyCost },
                errorLogs = analysis.errorSignals.map { it.title },
                todayAnalysis = analysis,
                rankedTasks = rankedTasks
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(date = date)
    )

    fun recordStrategyFeedback(feedbackType: RecommendationFeedbackType) {
        viewModelScope.launch {
            feedbackRepository.record(
                date = date,
                surface = RecommendationSurface.DAILY_STRATEGY,
                feedbackType = feedbackType
            )
        }
    }

    fun confirmEstimatedCheckIn() {
        val estimated = uiState.value.estimatedCheckIn ?: return
        viewModelScope.launch {
            checkInRepository.save(
                date = date,
                sleepHours = estimated.sleepHours,
                mood = estimated.mood,
                bodyCondition = estimated.bodyCondition,
                burdenLevel = estimated.burdenLevel,
                memo = estimated.memo
            )
        }
    }

    private fun buildWidgetSnapshot(
        analysis: TodaySystemAnalysis,
        forecast: FutureLoadForecast,
        rebalancedPlan: RebalancedDayPlan,
        recoveryPlan: RecoveryPlan
    ): WidgetSnapshot {
        val highestRisk = forecast.highestRisk
        val overload = highestRisk?.let { "${it.timeBlock.label} ${it.riskLevel.label}" } ?: "계산 중"
        val nextAction = rebalancedPlan.nowTasks.firstOrNull()?.task?.title
            ?: recoveryPlan.actions.firstOrNull()?.title
            ?: "오늘의 운영 전략 생성"

        return WidgetSnapshot(
            mode = analysis.mode.userFacingLabel(),
            battery = "${analysis.lifeBattery}%",
            overload = overload,
            nextAction = nextAction
        )
    }

    private fun TodayMode.userFacingLabel(): String = when (this) {
        TodayMode.PERFORMANCE,
        TodayMode.FOCUS -> "집중 모드"

        TodayMode.BALANCED,
        TodayMode.MAINTENANCE -> "보통 모드"

        TodayMode.LOW_POWER -> "저전력 모드"

        TodayMode.RECOVERY,
        TodayMode.EMERGENCY -> "회복 모드"
    }
}
