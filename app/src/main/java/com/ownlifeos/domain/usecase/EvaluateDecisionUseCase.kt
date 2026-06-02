package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.DecisionRegretPredictor
import com.ownlifeos.domain.model.TodaySystemAnalysis

class EvaluateDecisionUseCase {
    fun execute(
        title: String,
        expectedEnergyCost: Int,
        urgency: Int,
        reversibility: Int,
        importance: Int,
        analysis: TodaySystemAnalysis
    ) = DecisionRegretPredictor.predict(
        title = title,
        expectedEnergyCost = expectedEnergyCost,
        urgency = urgency,
        reversibility = reversibility,
        importance = importance,
        analysis = analysis
    )
}
