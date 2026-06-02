package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.model.LifeSimulationInput
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.prediction.LifeSimulator

class RunLifeSimulationUseCase {
    fun execute(
        date: String,
        input: LifeSimulationInput,
        analysis: TodaySystemAnalysis
    ) = LifeSimulator.simulate(date, input, analysis)
}
