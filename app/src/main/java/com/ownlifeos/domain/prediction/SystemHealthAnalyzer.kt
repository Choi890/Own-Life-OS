package com.ownlifeos.domain.prediction

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.TodaySystemAnalyzer
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.SystemHealthReport
import com.ownlifeos.domain.model.TaskStatus
import java.time.LocalDate
import kotlin.math.abs

object SystemHealthAnalyzer {
    fun analyze(
        endDate: String,
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>
    ): SystemHealthReport {
        val end = LocalDate.parse(endDate)
        val start = end.minusDays(6).toString()
        val analyses = (0..6).map { offset ->
            val date = end.minusDays((6 - offset).toLong()).toString()
            TodaySystemAnalyzer.analyze(
                AnalysisInputs(
                    date = date,
                    todayCheckIn = checkIns.firstOrNull { it.date == date },
                    todayTasks = tasks.filter { it.date == date },
                    todayReview = reviews.firstOrNull { it.date == date },
                    recentCheckIns = checkIns.filter { it.date <= date },
                    recentTasks = tasks.filter { it.date <= date },
                    recentReviews = reviews.filter { it.date <= date }
                )
            )
        }

        val avgBattery = analyses.map { it.lifeBattery }.averageOrZero()
        val avgStress = analyses.map { it.stressLoad }.averageOrZero()
        val avgFatigue = analyses.map { it.fatigueLoad }.averageOrZero()
        val batterySwing = analyses.map { it.lifeBattery }.swing()
        val unfinished = tasks.count { it.status != TaskStatus.DONE }
        val reviewErrorCount = reviews.sumOf { it.errorLogs.lines().count { line -> line.isNotBlank() } }

        val score = (
            100 -
                (100 - avgBattery) * 0.26 -
                avgStress * 0.24 -
                avgFatigue * 0.20 -
                batterySwing * 0.16 -
                unfinished.coerceAtMost(10) * 2.2 -
                reviewErrorCount.coerceAtMost(10) * 1.6
            ).toInt().coerceIn(0, 100)

        val stable = buildList {
            if (avgBattery >= 60) add("Life Battery 평균이 안정적입니다.")
            if (batterySwing <= 25) add("배터리 변동 폭이 크지 않습니다.")
            if (unfinished <= 3) add("미완료 작업 누적이 낮은 편입니다.")
            if (isEmpty()) add("안정 영역은 아직 더 많은 기록이 필요합니다.")
        }

        val unstable = buildList {
            if (avgStress >= 60) add("스트레스 부하가 높은 날이 누적됩니다.")
            if (avgFatigue >= 60) add("피로 누적 지표가 높게 유지됩니다.")
            if (unfinished >= 5) add("미완료 작업이 다음 날로 자주 넘어갑니다.")
            if (reviewErrorCount >= 4) add("회고 오류 로그가 반복됩니다.")
            if (isEmpty()) add("큰 불안정 영역은 감지되지 않았습니다.")
        }

        val strategies = buildList {
            if (avgStress >= 60) add("고집중 작업은 오전에 1개만 배치하세요.")
            if (avgFatigue >= 60) add("주 2회 이상 10분 회복 플랜을 먼저 예약하세요.")
            if (unfinished >= 5) add("미완료 작업을 삭제/분할/연기 중 하나로 정리하세요.")
            if (batterySwing > 25) add("수면과 저녁 작업 시작 시간을 함께 점검하세요.")
            if (isEmpty()) add("현재 전략을 유지하고 마감 임박 작업만 선별하세요.")
        }

        return SystemHealthReport(
            weekStartDate = start,
            weekEndDate = endDate,
            healthScore = score,
            stableAreas = stable,
            unstableAreas = unstable,
            nextWeekStrategies = strategies,
            reasons = listOf(
                Reason(
                    title = "7일 평균",
                    detail = "배터리 ${avgBattery}점, 스트레스 ${avgStress}점, 피로 ${avgFatigue}점입니다.",
                    impact = ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                ),
                Reason(
                    title = "작업 누적",
                    detail = "미완료 작업 ${unfinished}개와 회고 오류 ${reviewErrorCount}개를 반영했습니다.",
                    impact = if (unfinished >= 5 || reviewErrorCount >= 4) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.TASK
                )
            )
        )
    }

    private fun List<Int>.averageOrZero(): Int = if (isEmpty()) 0 else average().toInt().coerceIn(0, 100)

    private fun List<Int>.swing(): Int = if (isEmpty()) 0 else abs((maxOrNull() ?: 0) - (minOrNull() ?: 0))
}
