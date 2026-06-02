package com.ownlifeos.domain.model

data class DailyCheckIn(
    val id: Long,
    val date: String,
    val sleepHours: Double,
    val mood: Int,
    val bodyCondition: Int,
    val burdenLevel: Int,
    val memo: String
)

data class DailyTask(
    val id: Long,
    val date: String,
    val title: String,
    val importance: Int,
    val energyCost: Int,
    val status: TaskStatus,
    val deadlineDate: String? = null,
    val focusNeed: Int = 3,
    val deferredCount: Int = 0,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val lastDeferredAt: Long? = null
)

data class DailyReview(
    val id: Long,
    val date: String,
    val goodThings: String,
    val errorLogs: String,
    val carryOver: String
)

data class LifeMetrics(
    val lifeBattery: Int = 50,
    val focusLevel: Int = 50,
    val stressLevel: Int = 50,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val activeEnergyCost: Int = 0,
    val recommendedMode: RecommendedMode = RecommendedMode.STEADY,
    val errorLogs: List<String> = emptyList(),
    val todayAnalysis: TodaySystemAnalysis? = null,
    val rankedTasks: List<RankedTask> = emptyList()
)
