package com.ownlifeos

import android.content.Context
import com.ownlifeos.data.local.AppDatabase
import com.ownlifeos.data.repository.CheckInRepository
import com.ownlifeos.data.repository.DailyMetricRepository
import com.ownlifeos.data.repository.DecisionRepository
import com.ownlifeos.data.repository.ForecastRepository
import com.ownlifeos.data.repository.LifeSimulationRepository
import com.ownlifeos.data.repository.RecoveryPlanRepository
import com.ownlifeos.data.repository.RecommendationFeedbackRepository
import com.ownlifeos.data.repository.ReviewRepository
import com.ownlifeos.data.repository.SystemHealthRepository
import com.ownlifeos.data.repository.TaskRepository
import com.ownlifeos.widget.WidgetSnapshotStore

class AppContainer(context: Context) {
    // Repositories share the singleton Room database so screens observe one consistent local state.
    private val database = AppDatabase.getInstance(context)

    val checkInRepository = CheckInRepository(database.checkInDao())
    val taskRepository = TaskRepository(database.taskDao(), database.taskEventDao())
    val reviewRepository = ReviewRepository(database.reviewDao())
    val dailyMetricRepository = DailyMetricRepository(database.dailyMetricDao())
    val decisionRepository = DecisionRepository(database.decisionDao())
    val forecastRepository = ForecastRepository(database.forecastDao())
    val recoveryPlanRepository = RecoveryPlanRepository(database.recoveryPlanDao())
    val lifeSimulationRepository = LifeSimulationRepository(database.lifeSimulationDao())
    val systemHealthRepository = SystemHealthRepository(database.systemHealthReportDao())
    val recommendationFeedbackRepository = RecommendationFeedbackRepository(database.recommendationFeedbackDao())
    val widgetSnapshotStore = WidgetSnapshotStore(context.applicationContext)
}
