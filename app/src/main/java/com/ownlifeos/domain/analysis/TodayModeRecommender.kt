package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TodayMode

object TodayModeRecommender {
    fun recommend(
        inputs: AnalysisInputs,
        batteryAnalysis: BatteryAnalysis
    ): Pair<TodayMode, List<Reason>> {
        val activeTasks = inputs.todayTasks.filter { it.status != TaskStatus.DONE }
        val highImportance = activeTasks.count { it.importance >= 4 }
        val highFocus = activeTasks.count { it.focusNeed >= 4 }
        val overdueOrDeferred = activeTasks.count { it.deferredCount >= 2 }

        val mode = when {
            batteryAnalysis.stressLoad >= 84 || activeTasks.size >= 8 -> TodayMode.EMERGENCY
            batteryAnalysis.fatigueLoad >= 72 || batteryAnalysis.lifeBattery <= 32 -> TodayMode.RECOVERY
            batteryAnalysis.lifeBattery <= 45 -> TodayMode.LOW_POWER
            highImportance > 0 && highFocus > 0 && batteryAnalysis.focusLevel >= 64 -> TodayMode.FOCUS
            batteryAnalysis.lifeBattery >= 72 && batteryAnalysis.stressLoad <= 42 -> TodayMode.PERFORMANCE
            overdueOrDeferred >= 2 || activeTasks.size >= 5 -> TodayMode.MAINTENANCE
            else -> TodayMode.BALANCED
        }

        val reasons = buildList {
            add(
                ReasonBuilder.build(
                    title = "Life Battery",
                    detail = "현재 배터리는 ${batteryAnalysis.lifeBattery}점입니다.",
                    impact = impactForLowHigh(batteryAnalysis.lifeBattery, low = 45, high = 72),
                    source = ReasonSource.HISTORY
                )
            )
            add(
                ReasonBuilder.build(
                    title = "Stress Load",
                    detail = "현재 작업 부하와 부담감 기준 스트레스 부하는 ${batteryAnalysis.stressLoad}점입니다.",
                    impact = if (batteryAnalysis.stressLoad >= 70) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.STRESS
                )
            )
            if (highImportance > 0) {
                add(
                    ReasonBuilder.build(
                        title = "중요 작업",
                        detail = "중요도 높은 작업 ${highImportance}개가 큐에 있습니다.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.TASK
                    )
                )
            }
            if (overdueOrDeferred > 0) {
                add(
                    ReasonBuilder.build(
                        title = "미룬 작업",
                        detail = "미룬 횟수가 높은 작업 ${overdueOrDeferred}개가 정리 대상입니다.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.TASK
                    )
                )
            }
        }

        return mode to reasons
    }

    private fun impactForLowHigh(value: Int, low: Int, high: Int): ReasonImpact = when {
        value <= low -> ReasonImpact.HIGH
        value >= high -> ReasonImpact.MEDIUM
        else -> ReasonImpact.LOW
    }
}
