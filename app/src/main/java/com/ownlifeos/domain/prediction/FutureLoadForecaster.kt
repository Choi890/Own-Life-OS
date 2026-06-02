package com.ownlifeos.domain.prediction

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.ForecastResult
import com.ownlifeos.domain.model.ForecastRiskLevel
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TimeBlock
import com.ownlifeos.domain.model.TodaySystemAnalysis
import kotlin.math.roundToInt

object FutureLoadForecaster {
    fun forecast(
        inputs: AnalysisInputs,
        analysis: TodaySystemAnalysis
    ): FutureLoadForecast {
        val activeTasks = inputs.todayTasks.filter { it.status != TaskStatus.DONE }
        val activeEnergy = activeTasks.sumOf { it.energyCost }
        val carryOver = inputs.recentTasks.count { it.date != inputs.date && it.status != TaskStatus.DONE }
        val sleepHours = inputs.todayCheckIn?.sleepHours ?: 7.0

        val results = TimeBlock.entries.map { block ->
            val blockWeight = when (block) {
                TimeBlock.MORNING -> -8
                TimeBlock.AFTERNOON -> 5
                TimeBlock.EVENING -> 14
            }
            val riskScore = (
                analysis.stressLoad * 0.34 +
                    analysis.fatigueLoad * 0.26 +
                    activeEnergy * 2.4 +
                    activeTasks.size * 3.0 +
                    carryOver * 4.0 +
                    sleepPenalty(sleepHours) -
                    analysis.lifeBattery * 0.18 +
                    blockWeight
                ).roundToInt().coerceIn(0, 100)

            val riskLevel = when {
                riskScore >= 70 -> ForecastRiskLevel.HIGH_RISK
                riskScore >= 42 -> ForecastRiskLevel.CAUTION
                else -> ForecastRiskLevel.STABLE
            }

            ForecastResult(
                date = inputs.date,
                timeBlock = block,
                riskLevel = riskLevel,
                riskScore = riskScore,
                summary = summaryFor(block, riskLevel),
                reasons = reasonsFor(
                    block = block,
                    analysis = analysis,
                    activeTaskCount = activeTasks.size,
                    activeEnergy = activeEnergy,
                    carryOver = carryOver,
                    sleepHours = sleepHours
                )
            )
        }

        return FutureLoadForecast(
            date = inputs.date,
            results = results
        )
    }

    private fun sleepPenalty(sleepHours: Double): Double = when {
        sleepHours < 5.0 -> 22.0
        sleepHours < 6.0 -> 14.0
        sleepHours < 7.0 -> 7.0
        else -> 0.0
    }

    private fun summaryFor(block: TimeBlock, level: ForecastRiskLevel): String = when (level) {
        ForecastRiskLevel.STABLE -> "${block.label} 부하는 안정적으로 유지될 가능성이 있습니다."
        ForecastRiskLevel.CAUTION -> "${block.label}에는 작업 부하가 커질 수 있습니다."
        ForecastRiskLevel.HIGH_RISK -> "${block.label} 과부하 가능성이 높아질 수 있습니다."
    }

    private fun reasonsFor(
        block: TimeBlock,
        analysis: TodaySystemAnalysis,
        activeTaskCount: Int,
        activeEnergy: Int,
        carryOver: Int,
        sleepHours: Double
    ): List<Reason> = buildList {
        add(
            Reason(
                title = "현재 시스템 상태",
                detail = "배터리 ${analysis.lifeBattery}점, 스트레스 부하 ${analysis.stressLoad}점, 피로 누적 ${analysis.fatigueLoad}점입니다.",
                impact = if (analysis.stressLoad >= 65 || analysis.fatigueLoad >= 65) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.HISTORY
            )
        )
        add(
            Reason(
                title = "남은 작업 부하",
                detail = "남은 작업 ${activeTaskCount}개, 예상 에너지 ${activeEnergy}점이 시간대별 예측에 반영되었습니다.",
                impact = if (activeEnergy >= 15 || activeTaskCount >= 6) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.TASK
            )
        )
        if (sleepHours < 7.0) {
            add(
                Reason(
                    title = "수면 기반 보정",
                    detail = "수면 ${sleepHours}시간으로 오후/저녁 부하 가능성을 더 보수적으로 계산했습니다.",
                    impact = if (sleepHours < 6.0) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.SLEEP
                )
            )
        }
        if (carryOver > 0) {
            add(
                Reason(
                    title = "미완료 누적",
                    detail = "최근 미완료 작업 ${carryOver}개가 남은 부하에 포함되었습니다.",
                    impact = if (carryOver >= 4) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
        }
        if (block == TimeBlock.EVENING) {
            add(
                Reason(
                    title = "저녁 시간대 보정",
                    detail = "저녁에는 피로 누적 가능성을 더 크게 반영했습니다.",
                    impact = ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
        }
    }
}
