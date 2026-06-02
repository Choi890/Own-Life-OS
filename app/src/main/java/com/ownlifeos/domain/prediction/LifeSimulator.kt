package com.ownlifeos.domain.prediction

import com.ownlifeos.domain.model.LifeSimulationInput
import com.ownlifeos.domain.model.LifeSimulationResult
import com.ownlifeos.domain.model.PossibilityLevel
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.TodaySystemAnalysis

object LifeSimulator {
    fun simulate(
        date: String,
        input: LifeSimulationInput,
        analysis: TodaySystemAnalysis
    ): LifeSimulationResult {
        val energy = input.expectedEnergyCost.coerceIn(1, 5)
        val durationPenalty = when {
            input.expectedDurationMinutes >= 180 -> 22
            input.expectedDurationMinutes >= 90 -> 12
            input.expectedDurationMinutes >= 45 -> 6
            else -> 0
        }
        val completionScore = (
            analysis.lifeBattery * 0.42 +
                analysis.focusLevel * 0.34 -
                analysis.stressLoad * 0.22 -
                analysis.fatigueLoad * 0.18 -
                energy * 7 -
                durationPenalty +
                input.importance.coerceIn(1, 5) * 4
            ).toInt().coerceIn(0, 100)

        val stressScore = (
            analysis.stressLoad * 0.44 +
                analysis.fatigueLoad * 0.26 +
                energy * 10 +
                input.urgency.coerceIn(1, 5) * 7 +
                durationPenalty -
                analysis.lifeBattery * 0.12
            ).toInt().coerceIn(0, 100)

        val regretScore = (
            stressScore * 0.42 +
                (100 - completionScore) * 0.36 +
                (6 - input.reversibility.coerceIn(1, 5)) * 8 -
                input.importance.coerceIn(1, 5) * 3
            ).toInt().coerceIn(0, 100)

        val completion = possibilityFromPositiveScore(completionScore)
        val stress = possibilityFromRiskScore(stressScore)
        val regret = possibilityFromRiskScore(regretScore)

        return LifeSimulationResult(
            date = date,
            title = input.title.ifBlank { "선택지" },
            completionPossibility = completion,
            stressImpact = stress,
            regretPossibility = regret,
            completionScore = completionScore,
            stressScore = stressScore,
            regretScore = regretScore,
            summary = "현재 상태에서는 완료 가능성 ${completion.label}, 스트레스 영향 ${stress.label}, 후회 가능성 ${regret.label}으로 볼 수 있습니다.",
            reasons = buildReasons(input, analysis, completionScore, stressScore, regretScore)
        )
    }

    private fun possibilityFromPositiveScore(score: Int): PossibilityLevel = when {
        score >= 68 -> PossibilityLevel.HIGH
        score >= 40 -> PossibilityLevel.MEDIUM
        else -> PossibilityLevel.LOW
    }

    private fun possibilityFromRiskScore(score: Int): PossibilityLevel = when {
        score >= 68 -> PossibilityLevel.HIGH
        score >= 40 -> PossibilityLevel.MEDIUM
        else -> PossibilityLevel.LOW
    }

    private fun buildReasons(
        input: LifeSimulationInput,
        analysis: TodaySystemAnalysis,
        completionScore: Int,
        stressScore: Int,
        regretScore: Int
    ): List<Reason> = listOf(
        Reason(
            title = "현재 상태",
            detail = "배터리 ${analysis.lifeBattery}점, 집중도 ${analysis.focusLevel}점, 스트레스 부하 ${analysis.stressLoad}점입니다.",
            impact = if (analysis.lifeBattery < 45 || analysis.stressLoad > 65) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
            source = ReasonSource.HISTORY
        ),
        Reason(
            title = "선택 비용",
            detail = "예상 에너지 ${input.expectedEnergyCost}점, 예상 시간 ${input.expectedDurationMinutes}분입니다.",
            impact = if (input.expectedEnergyCost >= 4 || input.expectedDurationMinutes >= 120) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
            source = ReasonSource.DECISION
        ),
        Reason(
            title = "가능성 점수",
            detail = "완료 ${completionScore}점, 스트레스 ${stressScore}점, 후회 ${regretScore}점으로 계산했습니다.",
            impact = ReasonImpact.MEDIUM,
            source = ReasonSource.DECISION
        )
    )
}
