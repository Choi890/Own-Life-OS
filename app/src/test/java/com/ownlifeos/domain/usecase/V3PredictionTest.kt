package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.TodaySystemAnalyzer
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.ForecastRiskLevel
import com.ownlifeos.domain.model.LifeSimulationInput
import com.ownlifeos.domain.model.RebalanceAction
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.prediction.DayRebalancer
import com.ownlifeos.domain.prediction.FutureLoadForecaster
import com.ownlifeos.domain.prediction.LifeSimulator
import com.ownlifeos.domain.prediction.RecoveryPlanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class V3PredictionTest {
    @Test
    fun highLoadForecastProducesRecoveryPlanWithActionableSteps() {
        val inputs = overloadedInputs()
        val analysis = TodaySystemAnalyzer.analyze(inputs)
        val forecast = FutureLoadForecaster.forecast(inputs, analysis)
        val plan = RecoveryPlanner.plan(inputs, analysis, forecast)

        assertTrue(forecast.results.any { it.riskLevel == ForecastRiskLevel.HIGH_RISK })
        assertTrue(plan.actions.isNotEmpty())
        assertTrue(plan.actions.all { it.durationMinutes > 0 && it.instruction.isNotBlank() })
        assertTrue(plan.reasons.isNotEmpty())
    }

    @Test
    fun lowBatteryRebalancerPutsEasyTaskBeforeHardTask() {
        val inputs = overloadedInputs()
        val analysis = TodaySystemAnalyzer.analyze(inputs)
        val tasks = listOf(
            DailyTask(1, "2026-06-01", "쉬운 정리", 3, 1, TaskStatus.WAITING, focusNeed = 1),
            DailyTask(2, "2026-06-01", "어려운 기획", 3, 5, TaskStatus.WAITING, focusNeed = 5)
        )

        val plan = DayRebalancer.rebalance("2026-06-01", tasks, analysis)

        assertEquals("쉬운 정리", plan.nowTasks.first().task.title)
        assertTrue(plan.deferCandidates.any { it.task.title == "어려운 기획" } || plan.laterTasks.any { it.task.title == "어려운 기획" })
    }

    @Test
    fun simulationUsesPossibilityLanguageAndReasons() {
        val inputs = overloadedInputs()
        val analysis = TodaySystemAnalyzer.analyze(inputs)

        val result = LifeSimulator.simulate(
            date = "2026-06-01",
            input = LifeSimulationInput(
                title = "오늘 밤 새 프로젝트 시작하기",
                description = "",
                expectedEnergyCost = 5,
                expectedDurationMinutes = 180,
                urgency = 4,
                reversibility = 2,
                importance = 3
            ),
            analysis = analysis
        )

        assertTrue(result.summary.contains("가능성"))
        assertTrue(result.reasons.isNotEmpty())
        assertTrue(result.stressScore >= 50)
    }

    private fun overloadedInputs(): AnalysisInputs = AnalysisInputs(
        date = "2026-06-01",
        todayCheckIn = DailyCheckIn(1, "2026-06-01", 4.5, 2, 2, 5, ""),
        todayTasks = (1..7).map {
            DailyTask(
                id = it.toLong(),
                date = "2026-06-01",
                title = "작업 $it",
                importance = 4,
                energyCost = 4,
                status = TaskStatus.WAITING,
                focusNeed = 4
            )
        },
        todayReview = null,
        recentCheckIns = emptyList(),
        recentTasks = emptyList(),
        recentReviews = emptyList()
    )
}
