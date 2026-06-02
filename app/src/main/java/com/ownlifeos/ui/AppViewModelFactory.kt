package com.ownlifeos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ownlifeos.AppContainer
import com.ownlifeos.ui.screens.decision.DecisionCheckViewModel
import com.ownlifeos.ui.screens.evening.EveningReviewViewModel
import com.ownlifeos.ui.screens.forecast.ForecastViewModel
import com.ownlifeos.ui.screens.health.SystemHealthViewModel
import com.ownlifeos.ui.screens.home.HomeViewModel
import com.ownlifeos.ui.screens.morning.MorningCheckInViewModel
import com.ownlifeos.ui.screens.pattern.OperatingPatternViewModel
import com.ownlifeos.ui.screens.report.WeeklyReportViewModel
import com.ownlifeos.ui.screens.simulation.LifeSimulationViewModel
import com.ownlifeos.ui.screens.tasks.TaskQueueViewModel

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository,
                feedbackRepository = container.recommendationFeedbackRepository,
                widgetSnapshotStore = container.widgetSnapshotStore
            )
            modelClass.isAssignableFrom(MorningCheckInViewModel::class.java) -> MorningCheckInViewModel(
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(TaskQueueViewModel::class.java) -> TaskQueueViewModel(
                taskRepository = container.taskRepository,
                checkInRepository = container.checkInRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(EveningReviewViewModel::class.java) -> EveningReviewViewModel(
                reviewRepository = container.reviewRepository,
                taskRepository = container.taskRepository
            )
            modelClass.isAssignableFrom(WeeklyReportViewModel::class.java) -> WeeklyReportViewModel(
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(DecisionCheckViewModel::class.java) -> DecisionCheckViewModel(
                decisionRepository = container.decisionRepository,
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(ForecastViewModel::class.java) -> ForecastViewModel(
                forecastRepository = container.forecastRepository,
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(LifeSimulationViewModel::class.java) -> LifeSimulationViewModel(
                simulationRepository = container.lifeSimulationRepository,
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(SystemHealthViewModel::class.java) -> SystemHealthViewModel(
                systemHealthRepository = container.systemHealthRepository,
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository
            )
            modelClass.isAssignableFrom(OperatingPatternViewModel::class.java) -> OperatingPatternViewModel(
                checkInRepository = container.checkInRepository,
                taskRepository = container.taskRepository,
                reviewRepository = container.reviewRepository,
                feedbackRepository = container.recommendationFeedbackRepository
            )
            else -> error("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}
