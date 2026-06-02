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
    // AppContainer는 앱 전체의 의존성 조립 지점이다.
    // Room 데이터베이스를 한 번만 만들고, 각 기능 Repository가 같은 DAO 인스턴스를 공유하게 한다.
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
