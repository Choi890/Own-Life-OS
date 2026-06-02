package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.TodaySystemAnalysis

object TodaySystemAnalyzer {
    fun analyze(inputs: AnalysisInputs): TodaySystemAnalysis {
        val battery = LifeBatteryAnalyzer.analyze(inputs)
        val (mode, modeReasons) = TodayModeRecommender.recommend(inputs, battery)
        val errorSignals = ErrorLogGenerator.generate(inputs, battery)

        return TodaySystemAnalysis(
            date = inputs.date,
            lifeBattery = battery.lifeBattery,
            focusLevel = battery.focusLevel,
            stressLoad = battery.stressLoad,
            fatigueLoad = battery.fatigueLoad,
            mode = mode,
            modeReasons = modeReasons,
            batteryReasons = battery.reasons,
            batteryFactors = battery.factors,
            errorSignals = errorSignals
        )
    }
}
