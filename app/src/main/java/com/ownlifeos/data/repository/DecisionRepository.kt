package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.DecisionDao
import com.ownlifeos.data.local.entity.DecisionEntity
import com.ownlifeos.data.local.entity.toDomain
import com.ownlifeos.domain.model.DecisionPrediction
import com.ownlifeos.domain.model.DecisionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DecisionRepository(
    private val dao: DecisionDao
) {
    fun observeRecent(): Flow<List<DecisionRecord>> =
        dao.observeRecent().map { decisions -> decisions.map { it.toDomain() } }

    suspend fun saveDecision(
        date: String,
        title: String,
        description: String,
        expectedEnergyCost: Int,
        urgency: Int,
        reversibility: Int,
        importance: Int,
        prediction: DecisionPrediction
    ) {
        dao.insertDecision(
            DecisionEntity(
                date = date,
                title = title.trim(),
                description = description.trim(),
                expectedEnergyCost = expectedEnergyCost.coerceIn(1, 5),
                urgency = urgency.coerceIn(1, 5),
                reversibility = reversibility.coerceIn(1, 5),
                importance = importance.coerceIn(1, 5),
                predictedRiskScore = prediction.riskScore,
                predictedRiskLevel = prediction.riskLevel.name,
                reasons = prediction.reasons.joinToString(separator = "\n") {
                    "${it.title}: ${it.detail}"
                }
            )
        )
    }
}
