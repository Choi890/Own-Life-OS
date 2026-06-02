package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.DecisionPrediction
import com.ownlifeos.domain.model.DecisionRiskLevel
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource

object DecisionRegretPredictor {
    fun predict(
        title: String,
        expectedEnergyCost: Int,
        urgency: Int,
        reversibility: Int,
        importance: Int,
        analysis: com.ownlifeos.domain.model.TodaySystemAnalysis
    ): DecisionPrediction {
        val energy = expectedEnergyCost.coerceIn(1, 5)
        val urgent = urgency.coerceIn(1, 5)
        val reversible = reversibility.coerceIn(1, 5)
        val important = importance.coerceIn(1, 5)

        val score = (
            energy * 12 +
                urgent * 7 +
                (6 - reversible) * 10 +
                analysis.stressLoad * 0.32 +
                analysis.fatigueLoad * 0.26 -
                analysis.lifeBattery * 0.18 -
                important * 4
            ).toInt().coerceIn(0, 100)

        val riskLevel = when {
            score >= 70 -> DecisionRiskLevel.HIGH
            score >= 42 -> DecisionRiskLevel.MEDIUM
            else -> DecisionRiskLevel.LOW
        }

        val reasons = buildList {
            add(
                ReasonBuilder.build(
                    title = "현재 상태",
                    detail = "배터리 ${analysis.lifeBattery}점, 스트레스 부하 ${analysis.stressLoad}점, 피로 누적 ${analysis.fatigueLoad}점입니다.",
                    impact = if (analysis.stressLoad >= 65 || analysis.fatigueLoad >= 65) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
            add(
                ReasonBuilder.build(
                    title = "선택 비용",
                    detail = "예상 에너지 ${energy}점, 긴급도 ${urgent}점, 되돌리기 쉬움 ${reversible}점입니다.",
                    impact = if (energy >= 4 || reversible <= 2) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.DECISION
                )
            )
            if (important >= 4) {
                add(
                    ReasonBuilder.build(
                        title = "중요도",
                        detail = "중요도가 높아 단순 회피보다 범위 조정이 더 적합합니다.",
                        impact = ReasonImpact.MEDIUM,
                        source = ReasonSource.DECISION
                    )
                )
            }
        }

        val recommendation = when (riskLevel) {
            DecisionRiskLevel.HIGH -> "바로 실행하기보다 범위를 줄이거나 내일 다시 확인하세요."
            DecisionRiskLevel.MEDIUM -> "작게 실행하고, 되돌릴 수 있는 조건을 먼저 정하세요."
            DecisionRiskLevel.LOW -> "현재 상태에서는 실행 부담이 낮은 편입니다."
        }

        return DecisionPrediction(
            title = title.ifBlank { "선택" },
            riskScore = score,
            riskLevel = riskLevel,
            reasons = reasons,
            recommendation = recommendation
        )
    }
}
