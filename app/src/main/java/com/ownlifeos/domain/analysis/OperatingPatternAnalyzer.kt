package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.OperatingPatternReport
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.TaskStatus
import java.time.Instant
import java.time.ZoneId

object OperatingPatternAnalyzer {
    fun analyze(
        startDate: String,
        endDate: String,
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>,
        feedbacks: List<RecommendationFeedback>
    ): OperatingPatternReport {
        val completedTasks = tasks.filter { it.status == TaskStatus.DONE && it.completedAt != null }
        val goodTimeWindow = completedTasks
            .mapNotNull { it.completedAt?.toHour() }
            .mostCommonHourWindow()
            ?: "기록이 더 쌓이면 표시됩니다"

        val lateStartedCount = tasks.count { (it.startedAt?.toHour() ?: -1) >= 22 }
        val tiredReviewCount = reviews.count {
            "${it.goodThings} ${it.errorLogs} ${it.carryOver}".contains("피로") ||
                "${it.goodThings} ${it.errorLogs} ${it.carryOver}".contains("무리")
        }
        val riskTimeWindow = when {
            lateStartedCount >= 2 -> "밤 10시 이후"
            tiredReviewCount >= 2 -> "저녁 이후"
            else -> "아직 뚜렷한 위험 시간대가 없습니다"
        }

        val lowSleepDays = checkIns.count { it.sleepHours < 6.0 }
        val heavyTaskDays = tasks
            .filter { it.status != TaskStatus.DONE }
            .groupBy { it.date }
            .count { (_, dayTasks) -> dayTasks.size >= 5 || dayTasks.sumOf { it.energyCost } >= 14 }
        val failureCondition = when {
            lowSleepDays >= 2 && heavyTaskDays >= 1 -> "수면 6시간 미만 + 남은 작업 5개 이상"
            lowSleepDays >= 2 -> "수면 6시간 미만인 날"
            heavyTaskDays >= 2 -> "남은 작업이 많고 에너지 소모가 큰 날"
            else -> "데이터가 쌓이면 실패 조건을 더 좁혀 표시합니다"
        }

        val matchedFeedbacks = feedbacks.count { it.feedbackType == RecommendationFeedbackType.MATCHED }
        val correctiveFeedbacks = feedbacks.size - matchedFeedbacks
        val feedbackSummary = when {
            feedbacks.isEmpty() -> "아직 추천 피드백이 없습니다"
            matchedFeedbacks >= correctiveFeedbacks -> "최근 추천은 대체로 맞았다는 피드백이 많습니다"
            else -> "최근 추천은 보정 피드백이 더 많아 다음 전략을 더 보수적으로 해석합니다"
        }

        return OperatingPatternReport(
            startDate = startDate,
            endDate = endDate,
            goodTimeWindow = goodTimeWindow,
            riskTimeWindow = riskTimeWindow,
            failureCondition = failureCondition,
            recoveryActions = listOf(
                "10분 산책",
                "책상 위 5분 정리",
                "쉬운 작업 하나 완료"
            ),
            feedbackSummary = feedbackSummary,
            reasons = buildReasons(checkIns, tasks, reviews, feedbacks)
        )
    }

    private fun Long.toHour(): Int =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).hour

    private fun List<Int>.mostCommonHourWindow(): String? {
        val hour = groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: return null
        return when (hour) {
            in 5..11 -> "오전 ${hour}시~${(hour + 2).coerceAtMost(12)}시"
            in 12..17 -> "오후 ${hour}시~${hour + 2}시"
            in 18..21 -> "저녁 ${hour}시~${hour + 2}시"
            else -> "밤 ${hour}시 이후"
        }
    }

    private fun buildReasons(
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>,
        feedbacks: List<RecommendationFeedback>
    ): List<Reason> = listOf(
        Reason(
            title = "분석 범위",
            detail = "최근 체크인 ${checkIns.size}개, 작업 ${tasks.size}개, 회고 ${reviews.size}개를 사용했습니다.",
            impact = ReasonImpact.MEDIUM,
            source = ReasonSource.HISTORY
        ),
        Reason(
            title = "추천 보정",
            detail = "최근 추천 피드백 ${feedbacks.size}개를 운영 패턴 해석에 반영했습니다.",
            impact = if (feedbacks.isEmpty()) ReasonImpact.LOW else ReasonImpact.MEDIUM,
            source = ReasonSource.DECISION
        )
    )
}
