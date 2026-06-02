package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.SystemHealthReportDao
import com.ownlifeos.data.local.entity.SystemHealthReportEntity
import com.ownlifeos.domain.model.SystemHealthReport

class SystemHealthRepository(
    private val dao: SystemHealthReportDao
) {
    suspend fun save(report: SystemHealthReport) {
        dao.insert(
            SystemHealthReportEntity(
                weekStartDate = report.weekStartDate,
                weekEndDate = report.weekEndDate,
                healthScore = report.healthScore,
                stableAreasJson = report.stableAreas.joinToString(separator = "\n"),
                unstableAreasJson = report.unstableAreas.joinToString(separator = "\n"),
                nextWeekStrategiesJson = report.nextWeekStrategies.joinToString(separator = "\n"),
                reasonsJson = report.reasons.joinToString(separator = "\n") {
                    "${it.title}: ${it.detail}"
                },
                generatedAt = report.generatedAt
            )
        )
    }
}
