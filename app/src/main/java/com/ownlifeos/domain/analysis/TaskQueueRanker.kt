package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RankedTask
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TaskStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object TaskQueueRanker {
    fun rank(
        tasks: List<DailyTask>,
        batteryAnalysis: BatteryAnalysis,
        today: String
    ): List<RankedTask> {
        val currentCondition = (batteryAnalysis.lifeBattery + batteryAnalysis.focusLevel - batteryAnalysis.stressLoad / 2)
            .coerceIn(0, 100)

        return tasks
            .filter { it.status != TaskStatus.DONE }
            .map { task ->
                val dueScore = deadlineScore(task.deadlineDate, today)
                val conditionFit = conditionFitScore(task, currentCondition)
                val score = (
                    task.importance * 18 +
                        dueScore +
                        task.deferredCount * 8 +
                        conditionFit -
                        task.energyCost * energyPenaltyMultiplier(currentCondition) -
                        focusPenalty(task, batteryAnalysis.focusLevel)
                    ).coerceIn(0, 100)

                RankedTask(
                    task = task,
                    score = score,
                    reasons = buildReasons(task, dueScore, conditionFit, currentCondition, batteryAnalysis.focusLevel)
                )
            }
            .sortedWith(
                compareByDescending<RankedTask> { it.score }
                    .thenByDescending { it.task.importance }
                    .thenBy { it.task.energyCost }
            )
    }

    private fun deadlineScore(deadlineDate: String?, today: String): Int {
        val deadline = deadlineDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return 0
        val days = ChronoUnit.DAYS.between(LocalDate.parse(today), deadline)
        return when {
            days < 0 -> 28
            days == 0L -> 24
            days == 1L -> 18
            days <= 3 -> 12
            else -> 4
        }
    }

    private fun conditionFitScore(task: DailyTask, currentCondition: Int): Int = when {
        currentCondition >= 70 && task.focusNeed >= 4 -> 18
        currentCondition >= 55 && task.energyCost <= 3 -> 12
        currentCondition < 45 && task.energyCost <= 2 -> 18
        currentCondition < 45 && task.energyCost >= 4 -> -12
        else -> 8
    }

    private fun energyPenaltyMultiplier(currentCondition: Int): Int = when {
        currentCondition < 45 -> 7
        currentCondition < 65 -> 5
        else -> 3
    }

    private fun focusPenalty(task: DailyTask, focusLevel: Int): Int =
        if (focusLevel < 45 && task.focusNeed >= 4) 12 else 0

    private fun buildReasons(
        task: DailyTask,
        dueScore: Int,
        conditionFit: Int,
        currentCondition: Int,
        focusLevel: Int
    ): List<Reason> = buildList {
        add(
            ReasonBuilder.build(
                title = "중요도",
                detail = "중요도 ${task.importance}점이 추천 순서에 반영되었습니다.",
                impact = if (task.importance >= 4) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.TASK
            )
        )
        if (dueScore > 0) {
            add(
                ReasonBuilder.build(
                    title = "마감일",
                    detail = "마감일 ${task.deadlineDate ?: "-"} 기준으로 ${dueScore}점이 더해졌습니다.",
                    impact = if (dueScore >= 18) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.TASK
                )
            )
        }
        if (task.deferredCount > 0) {
            add(
                ReasonBuilder.build(
                    title = "미룬 횟수",
                    detail = "미룬 횟수 ${task.deferredCount}회가 누적되어 순서가 올라갔습니다.",
                    impact = ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
        }
        add(
            ReasonBuilder.build(
                title = "현재 컨디션 적합도",
                detail = "현재 컨디션 ${currentCondition}점, 집중도 ${focusLevel}점 기준 적합도 ${conditionFit}점입니다.",
                impact = ReasonImpact.MEDIUM,
                source = ReasonSource.BODY
            )
        )
    }
}
