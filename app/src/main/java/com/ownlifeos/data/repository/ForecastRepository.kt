package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.ForecastDao
import com.ownlifeos.data.local.entity.ForecastResultEntity
import com.ownlifeos.domain.model.ForecastResult

class ForecastRepository(
    private val dao: ForecastDao
) {
    suspend fun replaceForDate(results: List<ForecastResult>) {
        if (results.isEmpty()) return
        val date = results.first().date
        dao.deleteByDate(date)
        dao.insertAll(
            results.map {
                ForecastResultEntity(
                    date = it.date,
                    timeBlock = it.timeBlock.name,
                    riskLevel = it.riskLevel.name,
                    riskScore = it.riskScore,
                    summary = it.summary,
                    reasonsJson = it.reasons.joinToString(separator = "\n") { reason ->
                        "${reason.title}: ${reason.detail}"
                    },
                    generatedAt = it.generatedAt
                )
            }
        )
    }
}
