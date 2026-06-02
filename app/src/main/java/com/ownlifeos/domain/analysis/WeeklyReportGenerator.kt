package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.WeeklySystemReport
import java.time.LocalDate
import kotlin.math.roundToInt

object WeeklyReportGenerator {
    fun generate(
        endDate: String,
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>
    ): WeeklySystemReport {
        val end = LocalDate.parse(endDate)
        val start = end.minusDays(6).toString()
        val analyses = (0..6).map { offset ->
            val date = end.minusDays((6 - offset).toLong()).toString()
            val inputs = AnalysisInputs(
                date = date,
                todayCheckIn = checkIns.firstOrNull { it.date == date },
                todayTasks = tasks.filter { it.date == date },
                todayReview = reviews.firstOrNull { it.date == date },
                recentCheckIns = checkIns.filter { it.date <= date },
                recentTasks = tasks.filter { it.date <= date },
                recentReviews = reviews.filter { it.date <= date }
            )
            TodaySystemAnalyzer.analyze(inputs)
        }

        val bestDay = analyses.maxByOrNull { it.lifeBattery - it.stressLoad / 2 }?.date
        val riskyDay = analyses.maxByOrNull { it.stressLoad + it.fatigueLoad }?.date
        val repeatedErrors = analyses
            .flatMap { it.errorSignals }
            .groupingBy { it.type.label }
            .eachCount()
            .filterValues { it >= 2 }
            .keys
            .toList()

        val unfinishedCount = tasks.count { it.status != TaskStatus.DONE }
        val highFatigueDays = analyses.count { it.fatigueLoad >= 65 }
        val highStressDays = analyses.count { it.stressLoad >= 65 }

        return WeeklySystemReport(
            startDate = start,
            endDate = endDate,
            averageLifeBattery = analyses.map { it.lifeBattery }.averageOrZero(),
            averageFocusLevel = analyses.map { it.focusLevel }.averageOrZero(),
            averageStressLoad = analyses.map { it.stressLoad }.averageOrZero(),
            bestDay = bestDay,
            riskyDay = riskyDay,
            repeatedErrors = repeatedErrors,
            nextWeekStrategies = buildStrategies(
                repeatedErrors = repeatedErrors,
                unfinishedCount = unfinishedCount,
                highFatigueDays = highFatigueDays,
                highStressDays = highStressDays
            ),
            reasons = listOf(
                ReasonBuilder.build(
                    title = "분석 범위",
                    detail = "${start}부터 ${endDate}까지 7일 기록을 집계했습니다.",
                    impact = ReasonImpact.LOW,
                    source = ReasonSource.HISTORY
                ),
                ReasonBuilder.build(
                    title = "미완료 큐",
                    detail = "이번 주 미완료 작업 ${unfinishedCount}개가 다음 주 전략에 반영되었습니다.",
                    impact = if (unfinishedCount >= 5) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.TASK
                )
            )
        )
    }

    private fun List<Int>.averageOrZero(): Int =
        if (isEmpty()) 0 else average().roundToInt().coerceIn(0, 100)

    private fun buildStrategies(
        repeatedErrors: List<String>,
        unfinishedCount: Int,
        highFatigueDays: Int,
        highStressDays: Int
    ): List<String> = buildList {
        if (highFatigueDays >= 2) add("다음 주 초반에는 낮은 에너지 작업과 회복 시간을 먼저 배치하세요.")
        if (highStressDays >= 2) add("작업 수를 줄이고, 하루 핵심 작업을 1개로 제한하세요.")
        if (unfinishedCount >= 5) add("미완료 작업을 삭제/위임/분할 중 하나로 정리하세요.")
        repeatedErrors.forEach { add("반복 오류 '$it'를 줄이는 작은 규칙을 하나 정하세요.") }
        if (isEmpty()) add("현재 패턴은 안정적입니다. 루틴을 유지하고 중요한 작업만 선별하세요.")
    }
}
