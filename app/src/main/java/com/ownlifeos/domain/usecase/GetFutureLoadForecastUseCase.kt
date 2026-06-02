package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.prediction.FutureLoadForecaster

class GetFutureLoadForecastUseCase {
    fun execute(
        inputs: AnalysisInputs,
        analysis: TodaySystemAnalysis
    ) = FutureLoadForecaster.forecast(inputs, analysis)
}
