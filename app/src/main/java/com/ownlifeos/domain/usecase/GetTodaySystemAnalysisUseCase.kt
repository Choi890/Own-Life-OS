package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.TodaySystemAnalyzer

class GetTodaySystemAnalysisUseCase {
    fun execute(inputs: AnalysisInputs) = TodaySystemAnalyzer.analyze(inputs)
}
