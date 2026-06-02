package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.DailyMetricDao
import com.ownlifeos.data.local.entity.DailyMetricEntity
import com.ownlifeos.domain.model.TodaySystemAnalysis

class DailyMetricRepository(
    private val dao: DailyMetricDao
) {
    suspend fun saveAnalysis(analysis: TodaySystemAnalysis) {
        dao.save(
            DailyMetricEntity(
                date = analysis.date,
                lifeBattery = analysis.lifeBattery,
                focusLevel = analysis.focusLevel,
                stressLoad = analysis.stressLoad,
                fatigueLoad = analysis.fatigueLoad,
                todayMode = analysis.mode.name,
                reasonSummary = analysis.modeReasons.joinToString(separator = "\n") {
                    "${it.title}: ${it.detail}"
                },
                generatedAt = analysis.generatedAt
            )
        )
    }
}
