package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.prediction.RecoveryPlanner

class GetRecoveryPlanUseCase {
    fun execute(
        inputs: AnalysisInputs,
        analysis: TodaySystemAnalysis,
        forecast: FutureLoadForecast
    ) = RecoveryPlanner.plan(inputs, analysis, forecast)
}
