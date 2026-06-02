package com.ownlifeos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ownlifeos.data.local.converter.TaskStatusConverter
import com.ownlifeos.data.local.dao.CheckInDao
import com.ownlifeos.data.local.dao.DailyMetricDao
import com.ownlifeos.data.local.dao.DecisionDao
import com.ownlifeos.data.local.dao.ErrorLogDao
import com.ownlifeos.data.local.dao.ForecastDao
import com.ownlifeos.data.local.dao.LifeSimulationDao
import com.ownlifeos.data.local.dao.RecoveryPlanDao
import com.ownlifeos.data.local.dao.RecommendationFeedbackDao
import com.ownlifeos.data.local.dao.ReviewDao
import com.ownlifeos.data.local.dao.SystemHealthReportDao
import com.ownlifeos.data.local.dao.TaskDao
import com.ownlifeos.data.local.dao.TaskEventDao
import com.ownlifeos.data.local.entity.DailyMetricEntity
import com.ownlifeos.data.local.entity.CheckInEntity
import com.ownlifeos.data.local.entity.DecisionEntity
import com.ownlifeos.data.local.entity.DecisionOutcomeEntity
import com.ownlifeos.data.local.entity.ForecastResultEntity
import com.ownlifeos.data.local.entity.GeneratedErrorLogEntity
import com.ownlifeos.data.local.entity.LifeSimulationEntity
import com.ownlifeos.data.local.entity.LifeTaskEntity
import com.ownlifeos.data.local.entity.RecoveryPlanEntity
import com.ownlifeos.data.local.entity.RecommendationFeedbackEntity
import com.ownlifeos.data.local.entity.ReviewEntity
import com.ownlifeos.data.local.entity.SystemHealthReportEntity
import com.ownlifeos.data.local.entity.TaskEventEntity
import com.ownlifeos.data.local.migration.Migrations

@Database(
    entities = [
        CheckInEntity::class,
        LifeTaskEntity::class,
        ReviewEntity::class,
        DailyMetricEntity::class,
        GeneratedErrorLogEntity::class,
        TaskEventEntity::class,
        DecisionEntity::class,
        DecisionOutcomeEntity::class,
        ForecastResultEntity::class,
        LifeSimulationEntity::class,
        RecoveryPlanEntity::class,
        SystemHealthReportEntity::class,
        RecommendationFeedbackEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(TaskStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun taskDao(): TaskDao
    abstract fun reviewDao(): ReviewDao
    abstract fun dailyMetricDao(): DailyMetricDao
    abstract fun errorLogDao(): ErrorLogDao
    abstract fun taskEventDao(): TaskEventDao
    abstract fun decisionDao(): DecisionDao
    abstract fun forecastDao(): ForecastDao
    abstract fun recoveryPlanDao(): RecoveryPlanDao
    abstract fun lifeSimulationDao(): LifeSimulationDao
    abstract fun systemHealthReportDao(): SystemHealthReportDao
    abstract fun recommendationFeedbackDao(): RecommendationFeedbackDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "own_life_os.db"
                )
                    .addMigrations(
                        Migrations.MIGRATION_1_2,
                        Migrations.MIGRATION_2_3,
                        Migrations.MIGRATION_3_4
                    )
                    .build()
                    .also { instance = it }
            }
    }
}
