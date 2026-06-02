package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.ErrorSignal
import com.ownlifeos.domain.model.ErrorSignalType
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TaskStatus
import java.time.Instant
import java.time.ZoneId

object ErrorLogGenerator {
    fun generate(
        inputs: AnalysisInputs,
        batteryAnalysis: BatteryAnalysis
    ): List<ErrorSignal> {
        val signals = mutableListOf<ErrorSignal>()
        val checkIn = inputs.todayCheckIn
        val activeTasks = inputs.todayTasks.filter { it.status != TaskStatus.DONE }
        val carryOver = inputs.recentTasks.count { it.date != inputs.date && it.status != TaskStatus.DONE }

        if (checkIn != null && checkIn.sleepHours < 6.0) {
            signals += ErrorSignal(
                type = ErrorSignalType.SLEEP_SHORTAGE,
                severity = if (checkIn.sleepHours < 5.0) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                title = "수면 부족 신호",
                detail = "오늘 수면 시간이 ${checkIn.sleepHours}시간으로 낮게 기록되었습니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "수면 시간",
                        detail = "수면 시간이 짧은 날은 고에너지 작업을 줄이는 쪽으로 추천합니다.",
                        impact = ReasonImpact.HIGH,
                        source = ReasonSource.SLEEP
                    )
                )
            )
        }

        if (activeTasks.size >= 6 || activeTasks.sumOf { it.energyCost } >= 18) {
            signals += ErrorSignal(
                type = ErrorSignalType.TASK_OVERLOAD,
                severity = ReasonImpact.HIGH,
                title = "작업 과부하 신호",
                detail = "현재 큐에 대기/진행 작업 ${activeTasks.size}개, 에너지 합계 ${activeTasks.sumOf { it.energyCost }}점이 있습니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "작업 큐 부하",
                        detail = "오늘 끝낼 일과 넘길 일을 분리하는 것이 좋습니다.",
                        impact = ReasonImpact.HIGH,
                        source = ReasonSource.TASK
                    )
                )
            )
        }

        if (carryOver >= 3) {
            signals += ErrorSignal(
                type = ErrorSignalType.CARRY_OVER_ACCUMULATION,
                severity = ReasonImpact.MEDIUM,
                title = "미완료 작업 누적",
                detail = "최근 미완료 작업 ${carryOver}개가 남아 있습니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "누적 큐",
                        detail = "반복적으로 넘어가는 작업을 줄이기 위해 범위를 작게 나누세요.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.HISTORY
                    )
                )
            )
        }

        val lateStarts = inputs.todayTasks.count { task ->
            val hour = task.startedAt?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).hour
            }
            hour != null && hour >= 21
        }
        if (lateStarts > 0) {
            signals += ErrorSignal(
                type = ErrorSignalType.LATE_TASK_START,
                severity = ReasonImpact.MEDIUM,
                title = "늦은 시간 새 작업 시작",
                detail = "21시 이후 시작된 작업이 ${lateStarts}개 있습니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "시작 시간",
                        detail = "늦은 시간에는 새 작업보다 닫기/정리 루틴을 권장합니다.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.TASK
                    )
                )
            )
        }

        val reviewFailures = inputs.recentReviews.sumOf { review ->
            review.errorLogs.lines().count { it.isNotBlank() }
        }
        if (reviewFailures >= 4) {
            signals += ErrorSignal(
                type = ErrorSignalType.REPEATED_FAILURE_PATTERN,
                severity = ReasonImpact.MEDIUM,
                title = "반복 오류 패턴",
                detail = "최근 회고에 오류 로그가 ${reviewFailures}개 기록되었습니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "회고 로그",
                        detail = "반복되는 오류는 다음 주 전략에서 먼저 줄일 대상으로 다룹니다.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.REVIEW
                    )
                )
            )
        }

        if (batteryAnalysis.fatigueLoad >= 70) {
            signals += ErrorSignal(
                type = ErrorSignalType.FATIGUE_ACCUMULATION,
                severity = ReasonImpact.HIGH,
                title = "피로 누적 신호",
                detail = "최근 3일 기준 피로 누적 지표가 ${batteryAnalysis.fatigueLoad}점입니다.",
                reasons = listOf(
                    ReasonBuilder.build(
                        title = "최근 패턴",
                        detail = "오늘은 낮은 에너지 작업과 회복 시간을 우선 배치하세요.",
                        impact = ReasonImpact.HIGH,
                        source = ReasonSource.HISTORY
                    )
                )
            )
        }

        return signals
    }
}
