package com.ownlifeos.domain.prediction

import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.RebalanceAction
import com.ownlifeos.domain.model.RebalancedDayPlan
import com.ownlifeos.domain.model.RebalancedTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TodaySystemAnalysis
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DayRebalancer {
    fun rebalance(
        date: String,
        tasks: List<DailyTask>,
        analysis: TodaySystemAnalysis
    ): RebalancedDayPlan {
        val activeTasks = tasks.filter { it.status != TaskStatus.DONE }
        val rebalanced = activeTasks
            .map { task -> rebalanceTask(date, task, analysis) }
            .sortedWith(compareByDescending<RebalancedTask> { it.score }.thenBy { it.task.energyCost })

        return RebalancedDayPlan(
            date = date,
            nowTasks = rebalanced.filter { it.action == RebalanceAction.DO_NOW }.take(3),
            laterTasks = rebalanced.filter { it.action == RebalanceAction.DO_AFTER_RECOVERY || it.action == RebalanceAction.SPLIT },
            deferCandidates = rebalanced.filter { it.action == RebalanceAction.DEFER || it.action == RebalanceAction.DROP_IF_OPTIONAL },
            quickWinTasks = rebalanced.filter { it.task.energyCost <= 2 }.take(3),
            reasons = buildPlanReasons(analysis, activeTasks)
        )
    }

    private fun rebalanceTask(
        date: String,
        task: DailyTask,
        analysis: TodaySystemAnalysis
    ): RebalancedTask {
        val dueBoost = dueBoost(date, task.deadlineDate)
        val lowBattery = analysis.lifeBattery <= 45
        val highStress = analysis.stressLoad >= 65
        val highDifficulty = task.energyCost >= 4 || task.focusNeed >= 4
        val quickWin = task.energyCost <= 2 && task.focusNeed <= 3

        val action = when {
            dueBoost >= 20 && task.importance >= 4 -> RebalanceAction.DO_NOW
            lowBattery && quickWin -> RebalanceAction.DO_NOW
            lowBattery && highDifficulty -> RebalanceAction.DO_AFTER_RECOVERY
            highStress && highDifficulty && dueBoost < 18 -> RebalanceAction.DEFER
            task.energyCost >= 5 -> RebalanceAction.SPLIT
            task.importance <= 2 && highStress -> RebalanceAction.DROP_IF_OPTIONAL
            else -> RebalanceAction.DO_NOW
        }

        val score = (
            task.importance * 16 +
                dueBoost +
                task.deferredCount * 7 +
                if (quickWin && lowBattery) 18 else 0 -
                if (highStress && highDifficulty) 14 else 0 -
                if (lowBattery && task.energyCost >= 4) 10 else 0
            ).coerceIn(0, 100)

        return RebalancedTask(
            task = task,
            action = action,
            score = score,
            reasons = buildTaskReasons(task, analysis, dueBoost, action)
        )
    }

    private fun dueBoost(date: String, deadline: String?): Int {
        val target = deadline?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return 0
        val days = ChronoUnit.DAYS.between(LocalDate.parse(date), target)
        return when {
            days < 0 -> 26
            days == 0L -> 24
            days == 1L -> 18
            days <= 3 -> 12
            else -> 4
        }
    }

    private fun buildTaskReasons(
        task: DailyTask,
        analysis: TodaySystemAnalysis,
        dueBoost: Int,
        action: RebalanceAction
    ): List<Reason> = buildList {
        add(
            Reason(
                title = "추천 액션",
                detail = "'${action.label}'로 배치했습니다.",
                impact = ReasonImpact.MEDIUM,
                source = ReasonSource.TASK
            )
        )
        add(
            Reason(
                title = "작업 비용",
                detail = "에너지 ${task.energyCost}점, 집중 필요도 ${task.focusNeed}점입니다.",
                impact = if (task.energyCost >= 4 || task.focusNeed >= 4) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.TASK
            )
        )
        if (dueBoost > 0) {
            add(
                Reason(
                    title = "마감 반영",
                    detail = "마감일 ${task.deadlineDate} 기준 ${dueBoost}점을 반영했습니다.",
                    impact = if (dueBoost >= 18) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.TASK
                )
            )
        }
        if (analysis.lifeBattery <= 45 || analysis.stressLoad >= 65) {
            add(
                Reason(
                    title = "현재 상태 보정",
                    detail = "배터리 ${analysis.lifeBattery}점, 스트레스 부하 ${analysis.stressLoad}점을 반영했습니다.",
                    impact = ReasonImpact.HIGH,
                    source = ReasonSource.HISTORY
                )
            )
        }
    }

    private fun buildPlanReasons(
        analysis: TodaySystemAnalysis,
        tasks: List<DailyTask>
    ): List<Reason> = listOf(
        Reason(
            title = "재조정 기준",
            detail = "남은 작업 ${tasks.size}개를 배터리 ${analysis.lifeBattery}점과 스트레스 부하 ${analysis.stressLoad}점 기준으로 재배치했습니다.",
            impact = ReasonImpact.MEDIUM,
            source = ReasonSource.TASK
        )
    )
}
