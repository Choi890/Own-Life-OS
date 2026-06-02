package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.BatteryFactor
import com.ownlifeos.domain.model.TaskStatus
import kotlin.math.roundToInt

data class BatteryAnalysis(
    val lifeBattery: Int,
    val focusLevel: Int,
    val stressLoad: Int,
    val fatigueLoad: Int,
    val reasons: List<Reason>,
    val factors: List<BatteryFactor>
)

object LifeBatteryAnalyzer {
    fun analyze(inputs: AnalysisInputs): BatteryAnalysis {
        val checkIn = inputs.todayCheckIn
        val activeTasks = inputs.todayTasks.filter { it.status != TaskStatus.DONE }
        val taskCount = activeTasks.size
        val activeEnergy = activeTasks.sumOf { it.energyCost }
        val highFocusTasks = activeTasks.count { it.focusNeed >= 4 }
        val carryOverCount = inputs.recentTasks.count { it.date != inputs.date && it.status != TaskStatus.DONE }
        val recentFatigue = calculateRecentFatigue(inputs)

        val sleepScore = checkIn?.sleepHours?.let { (it / 8.0 * 100).coerceIn(10.0, 100.0) } ?: 55.0
        val moodScore = checkIn?.mood?.toScore() ?: 55.0
        val bodyScore = checkIn?.bodyCondition?.toScore() ?: 55.0
        val burdenLevel = checkIn?.burdenLevel ?: 3

        val stressLoad = (
            burdenLevel * 15 +
                taskCount * 4 +
                activeEnergy * 2 +
                carryOverCount * 4 +
                highFocusTasks * 3 +
                recentFatigue * 0.22 -
                sleepScore * 0.08
            ).roundToInt().coerceIn(0, 100)

        val fatigueLoad = (
            (100 - sleepScore) * 0.36 +
                (100 - bodyScore) * 0.22 +
                activeEnergy * 2.0 +
                carryOverCount * 5 +
                recentFatigue * 0.42
            ).roundToInt().coerceIn(0, 100)

        val lifeBattery = (
            sleepScore * 0.28 +
                moodScore * 0.20 +
                bodyScore * 0.24 +
                (100 - stressLoad) * 0.18 +
                (100 - fatigueLoad) * 0.10
            ).roundToInt().coerceIn(0, 100)

        val focusLevel = (
            moodScore * 0.24 +
                bodyScore * 0.22 +
                sleepScore * 0.18 +
                (100 - stressLoad) * 0.22 -
                highFocusTasks * 3 +
                inputs.todayTasks.count { it.status == TaskStatus.DONE } * 3
            ).roundToInt().coerceIn(0, 100)

        return BatteryAnalysis(
            lifeBattery = lifeBattery,
            focusLevel = focusLevel,
            stressLoad = stressLoad,
            fatigueLoad = fatigueLoad,
            reasons = buildReasons(
                sleepScore = sleepScore.roundToInt(),
                moodScore = moodScore.roundToInt(),
                bodyScore = bodyScore.roundToInt(),
                burdenLevel = burdenLevel,
                taskCount = taskCount,
                activeEnergy = activeEnergy,
                carryOverCount = carryOverCount,
                recentFatigue = recentFatigue.roundToInt()
            ),
            factors = buildFactors(
                sleepScore = sleepScore.roundToInt(),
                moodScore = moodScore.roundToInt(),
                bodyScore = bodyScore.roundToInt(),
                burdenLevel = burdenLevel,
                taskCount = taskCount,
                activeEnergy = activeEnergy,
                carryOverCount = carryOverCount,
                recentFatigue = recentFatigue.roundToInt()
            )
        )
    }

    private fun Int.toScore(): Double = (coerceIn(1, 5) * 20).toDouble()

    private fun calculateRecentFatigue(inputs: AnalysisInputs): Double {
        val recentCheckInLoad = inputs.recentCheckIns
            .takeLast(3)
            .map {
                val sleepLoad = (8.0 - it.sleepHours).coerceAtLeast(0.0) * 9
                val bodyLoad = (5 - it.bodyCondition).coerceAtLeast(0) * 8
                val burdenLoad = it.burdenLevel * 7
                sleepLoad + bodyLoad + burdenLoad
            }
        val unfinishedLoad = inputs.recentTasks
            .filter { it.date != inputs.date && it.status != TaskStatus.DONE }
            .sumOf { it.energyCost }
            .coerceAtMost(30) * 2.0

        if (recentCheckInLoad.isEmpty()) return unfinishedLoad
        return (recentCheckInLoad.average() + unfinishedLoad).coerceIn(0.0, 100.0)
    }

    private fun buildReasons(
        sleepScore: Int,
        moodScore: Int,
        bodyScore: Int,
        burdenLevel: Int,
        taskCount: Int,
        activeEnergy: Int,
        carryOverCount: Int,
        recentFatigue: Int
    ): List<Reason> = buildList {
        add(
            ReasonBuilder.build(
                title = "수면 반영",
                detail = "수면 기반 점수 ${sleepScore}점이 배터리에 반영되었습니다.",
                impact = if (sleepScore < 55) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.SLEEP
            )
        )
        add(
            ReasonBuilder.build(
                title = "컨디션 반영",
                detail = "기분 ${moodScore}점, 몸 상태 ${bodyScore}점으로 오늘의 기본 처리량을 계산했습니다.",
                impact = ReasonImpact.MEDIUM,
                source = ReasonSource.BODY
            )
        )
        add(
            ReasonBuilder.build(
                title = "작업 부하",
                detail = "대기/진행 작업 ${taskCount}개, 예상 에너지 ${activeEnergy}점입니다.",
                impact = if (activeEnergy >= 12 || taskCount >= 5) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.TASK
            )
        )
        if (carryOverCount > 0) {
            add(
                ReasonBuilder.build(
                    title = "미완료 누적",
                    detail = "최근 미완료 작업 ${carryOverCount}개가 오늘 부하에 포함되었습니다.",
                    impact = if (carryOverCount >= 4) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
        }
        if (recentFatigue >= 55) {
            add(
                ReasonBuilder.build(
                    title = "최근 3일 피로 누적",
                    detail = "최근 기록 기준 피로 누적 지표가 ${recentFatigue}점입니다.",
                    impact = ReasonImpact.HIGH,
                    source = ReasonSource.HISTORY
                )
            )
        }
        if (burdenLevel >= 4) {
            add(
                ReasonBuilder.build(
                    title = "부담감 상승",
                    detail = "오늘의 부담감이 높아 작업 범위를 줄이는 쪽으로 계산했습니다.",
                    impact = ReasonImpact.HIGH,
                    source = ReasonSource.STRESS
                )
            )
        }
    }

    private fun buildFactors(
        sleepScore: Int,
        moodScore: Int,
        bodyScore: Int,
        burdenLevel: Int,
        taskCount: Int,
        activeEnergy: Int,
        carryOverCount: Int,
        recentFatigue: Int
    ): List<BatteryFactor> = buildList {
        addScoreFactor(
            score = sleepScore,
            neutral = 70,
            lowTitle = "수면 부족",
            highTitle = "수면 여유",
            lowDetail = "수면 점수가 낮아 오늘 처리량을 보수적으로 계산했습니다.",
            highDetail = "수면 점수가 안정권이라 배터리에 회복 요인으로 반영했습니다.",
            lowWeight = 0.55,
            highWeight = 0.25
        )
        addScoreFactor(
            score = moodScore,
            neutral = 60,
            lowTitle = "기분 저하",
            highTitle = "기분 안정",
            lowDetail = "기분 점수가 낮아 집중 여력을 낮게 계산했습니다.",
            highDetail = "기분 점수가 안정권이라 시작 여력을 높게 계산했습니다.",
            lowWeight = 0.35,
            highWeight = 0.20
        )
        addScoreFactor(
            score = bodyScore,
            neutral = 60,
            lowTitle = "몸 상태 저하",
            highTitle = "몸 상태 안정",
            lowDetail = "몸 상태 점수가 낮아 에너지 소모를 크게 반영했습니다.",
            highDetail = "몸 상태가 안정권이라 작업 여력에 회복 요인으로 반영했습니다.",
            lowWeight = 0.45,
            highWeight = 0.22
        )
        if (burdenLevel >= 4) {
            add(
                BatteryFactor(
                    title = "부담감 높음",
                    detail = "오늘 부담감이 높아 작업 범위를 줄이는 방향으로 계산했습니다.",
                    points = -(burdenLevel - 3) * 8
                )
            )
        } else if (burdenLevel <= 2) {
            add(
                BatteryFactor(
                    title = "부담감 낮음",
                    detail = "오늘 부담감이 낮아 운영 여유를 회복 요인으로 반영했습니다.",
                    points = (3 - burdenLevel) * 4
                )
            )
        }
        if (taskCount > 3) {
            add(
                BatteryFactor(
                    title = "남은 작업 ${taskCount}개",
                    detail = "대기/진행 작업이 많아 전환 비용과 피로 가능성을 반영했습니다.",
                    points = -((taskCount - 3) * 4).coerceAtMost(18)
                )
            )
        }
        if (activeEnergy >= 9) {
            add(
                BatteryFactor(
                    title = "작업 에너지 ${activeEnergy}점",
                    detail = "남은 작업의 예상 에너지 소모량이 높습니다.",
                    points = -((activeEnergy - 7) * 2).coerceAtMost(20)
                )
            )
        }
        if (carryOverCount > 0) {
            add(
                BatteryFactor(
                    title = "미완료 누적 ${carryOverCount}개",
                    detail = "최근 미완료 작업이 오늘의 시작 부담에 포함되었습니다.",
                    points = -(carryOverCount * 3).coerceAtMost(18)
                )
            )
        }
        if (recentFatigue >= 55) {
            add(
                BatteryFactor(
                    title = "최근 3일 피로 누적",
                    detail = "최근 기록에서 피로 누적 신호가 반복되었습니다.",
                    points = -(((recentFatigue - 45) * 0.30).roundToInt()).coerceAtMost(18)
                )
            )
        }
        if (taskCount in 1..2 && activeEnergy <= 5) {
            add(
                BatteryFactor(
                    title = "작업 범위 작음",
                    detail = "오늘 남은 작업 수와 에너지 소모량이 낮습니다.",
                    points = 5
                )
            )
        }
    }.filter { it.points != 0 }

    private fun MutableList<BatteryFactor>.addScoreFactor(
        score: Int,
        neutral: Int,
        lowTitle: String,
        highTitle: String,
        lowDetail: String,
        highDetail: String,
        lowWeight: Double,
        highWeight: Double
    ) {
        if (score < neutral) {
            add(
                BatteryFactor(
                    title = lowTitle,
                    detail = lowDetail,
                    points = -((neutral - score) * lowWeight).roundToInt().coerceAtLeast(1)
                )
            )
        } else if (score >= neutral + 10) {
            add(
                BatteryFactor(
                    title = highTitle,
                    detail = highDetail,
                    points = ((score - neutral) * highWeight).roundToInt().coerceAtLeast(1)
                )
            )
        }
    }
}
