package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.LifeMetrics
import com.ownlifeos.domain.model.RecommendedMode
import com.ownlifeos.domain.model.TaskStatus
import kotlin.math.roundToInt

object LifeMetricsCalculator {
    fun calculate(
        checkIn: DailyCheckIn?,
        tasks: List<DailyTask>,
        review: DailyReview?
    ): LifeMetrics {
        val completedTasks = tasks.count { it.status == TaskStatus.DONE }
        val activeTasks = tasks.filter { it.status != TaskStatus.DONE }
        val activeEnergyCost = activeTasks.sumOf { it.energyCost }
        val completionRatio = if (tasks.isEmpty()) 0.0 else completedTasks.toDouble() / tasks.size

        val sleepScore = checkIn?.sleepHours
            ?.let { ((it / 8.0) * 100.0).coerceIn(20.0, 100.0) }
            ?: 55.0
        val moodScore = checkIn?.mood?.scaleToHundred() ?: 55.0
        val bodyScore = checkIn?.bodyCondition?.scaleToHundred() ?: 55.0
        val burdenPenalty = checkIn?.burdenLevel?.times(12) ?: 18

        val battery = (
            sleepScore * 0.32 +
                moodScore * 0.22 +
                bodyScore * 0.26 +
                (100 - burdenPenalty) * 0.20 -
                activeEnergyCost * 2.2 +
                completionRatio * 14
            ).roundToInt().coerceIn(0, 100)

        val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val highPriorityPending = activeTasks.count { it.importance >= 4 }
        val focus = (
            moodScore * 0.24 +
                bodyScore * 0.24 +
                sleepScore * 0.20 +
                completionRatio * 22 -
                (checkIn?.burdenLevel ?: 3) * 7 -
                inProgressCount * 5
            ).roundToInt().coerceIn(0, 100)

        val reviewErrorCount = review?.errorLogs
            ?.lines()
            ?.count { it.isNotBlank() }
            ?: 0
        val stress = (
            (checkIn?.burdenLevel ?: 3) * 16 +
                highPriorityPending * 8 +
                activeEnergyCost * 2 +
                inProgressCount * 5 +
                reviewErrorCount * 4 -
                (sleepScore / 10)
            ).roundToInt().coerceIn(0, 100)

        val mode = when {
            battery <= 35 || stress >= 76 -> RecommendedMode.RECOVERY
            tasks.isNotEmpty() && completedTasks == tasks.size && review == null -> RecommendedMode.REVIEW
            highPriorityPending > 0 && focus >= 58 -> RecommendedMode.DEEP_FOCUS
            activeTasks.size >= 5 || stress >= 62 -> RecommendedMode.CLEAR_QUEUE
            else -> RecommendedMode.STEADY
        }

        return LifeMetrics(
            lifeBattery = battery,
            focusLevel = focus,
            stressLevel = stress,
            completedTasks = completedTasks,
            totalTasks = tasks.size,
            activeEnergyCost = activeEnergyCost,
            recommendedMode = mode,
            errorLogs = buildErrorLogs(review, activeTasks)
        )
    }

    private fun Int.scaleToHundred(): Double = (this.coerceIn(1, 5) * 20).toDouble()

    private fun buildErrorLogs(
        review: DailyReview?,
        activeTasks: List<DailyTask>
    ): List<String> {
        val reviewLogs = review?.errorLogs
            ?.lines()
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()

        if (reviewLogs.isNotEmpty()) return reviewLogs

        return activeTasks
            .filter { it.importance >= 4 && it.energyCost >= 4 }
            .take(3)
            .map { "고부담 작업 대기: ${it.title}" }
    }
}
