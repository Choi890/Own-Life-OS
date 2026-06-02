package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.LifeSimulationDao
import com.ownlifeos.data.local.entity.LifeSimulationEntity
import com.ownlifeos.domain.model.LifeSimulationResult

class LifeSimulationRepository(
    private val dao: LifeSimulationDao
) {
    suspend fun save(result: LifeSimulationResult, description: String) {
        dao.insert(
            LifeSimulationEntity(
                date = result.date,
                scenarioTitle = result.title,
                scenarioDescription = description,
                completionPossibility = result.completionPossibility.name,
                stressImpact = result.stressImpact.name,
                regretPossibility = result.regretPossibility.name,
                completionScore = result.completionScore,
                stressScore = result.stressScore,
                regretScore = result.regretScore,
                summary = result.summary,
                reasonsJson = result.reasons.joinToString(separator = "\n") {
                    "${it.title}: ${it.detail}"
                },
                createdAt = result.generatedAt
            )
        )
    }
}
