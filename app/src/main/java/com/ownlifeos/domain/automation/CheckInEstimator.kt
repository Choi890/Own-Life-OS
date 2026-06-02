package com.ownlifeos.domain.automation

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import kotlin.math.roundToInt

data class EstimatedCheckIn(
    val sleepHours: Double,
    val mood: Int,
    val bodyCondition: Int,
    val burdenLevel: Int,
    val memo: String,
    val reasons: List<String>
)

object CheckInEstimator {
    fun estimate(
        date: String,
        recentCheckIns: List<DailyCheckIn>,
        todayTasks: List<DailyTask>,
        recentTasks: List<DailyTask>,
        recentReviews: List<DailyReview>
    ): EstimatedCheckIn {
        val activeTasks = todayTasks.filter { it.status != TaskStatus.DONE }
        val activeEnergy = activeTasks.sumOf { it.energyCost }
        val recentSleep = recentCheckIns
            .filter { it.date != date }
            .takeLast(3)
            .map { it.sleepHours }
        val sleepHours = if (recentSleep.isEmpty()) 7.0 else {
            (recentSleep.average() * 2).roundToInt() / 2.0
        }.coerceIn(4.0, 9.0)
        val unfinishedRecent = recentTasks.count { it.date != date && it.status != TaskStatus.DONE }
        val fatigueReviewCount = recentReviews.count { review ->
            fatigueKeywords.any { keyword ->
                review.goodThings.contains(keyword) ||
                    review.errorLogs.contains(keyword) ||
                    review.carryOver.contains(keyword)
            }
        }

        val burdenLevel = when {
            activeTasks.size >= 5 || activeEnergy >= 14 || unfinishedRecent >= 4 -> 5
            activeTasks.size >= 3 || activeEnergy >= 8 || unfinishedRecent >= 2 -> 4
            activeTasks.isEmpty() || activeEnergy <= 3 -> 2
            else -> 3
        }
        val bodyCondition = when {
            sleepHours < 6.0 || fatigueReviewCount >= 2 -> 2
            sleepHours >= 7.5 && activeEnergy <= 6 && unfinishedRecent <= 1 -> 4
            else -> 3
        }
        val mood = when {
            burdenLevel >= 5 || fatigueReviewCount >= 2 -> 2
            bodyCondition >= 4 && burdenLevel <= 2 -> 4
            else -> 3
        }
        val memo = activeTasks
            .sortedWith(compareByDescending<DailyTask> { it.importance }.thenBy { it.energyCost })
            .firstOrNull()
            ?.title
            .orEmpty()

        return EstimatedCheckIn(
            sleepHours = sleepHours,
            mood = mood,
            bodyCondition = bodyCondition,
            burdenLevel = burdenLevel,
            memo = memo,
            reasons = buildReasons(
                activeTasks = activeTasks,
                activeEnergy = activeEnergy,
                unfinishedRecent = unfinishedRecent,
                fatigueReviewCount = fatigueReviewCount,
                sleepHours = sleepHours
            )
        )
    }

    private fun buildReasons(
        activeTasks: List<DailyTask>,
        activeEnergy: Int,
        unfinishedRecent: Int,
        fatigueReviewCount: Int,
        sleepHours: Double
    ): List<String> = buildList {
        if (activeTasks.isNotEmpty()) {
            add("오늘 남은 작업 ${activeTasks.size}개, 부담도 합계 ${activeEnergy}점")
        }
        if (unfinishedRecent > 0) {
            add("최근 미완료 작업 ${unfinishedRecent}개")
        }
        if (fatigueReviewCount > 0) {
            add("최근 회고에서 피로 신호 ${fatigueReviewCount}회")
        }
        if (sleepHours < 6.0) {
            add("최근 수면 입력값이 짧은 편")
        }
        if (isEmpty()) {
            add("최근 기록이 적어 보통 상태로 시작")
        }
    }

    private val fatigueKeywords = listOf("피로", "무리", "지침", "힘듦", "힘들", "회복")
}
