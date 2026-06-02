package com.ownlifeos.domain.model

data class DecisionRecord(
    val id: Long,
    val date: String,
    val title: String,
    val description: String,
    val expectedEnergyCost: Int,
    val urgency: Int,
    val reversibility: Int,
    val importance: Int,
    val predictedRiskScore: Int,
    val predictedRiskLevel: DecisionRiskLevel,
    val reasons: String,
    val createdAt: Long
)

data class DecisionOutcome(
    val id: Long,
    val decisionId: Long,
    val actualResult: String,
    val regretLevel: Int,
    val energyImpact: Int,
    val note: String,
    val recordedAt: Long
)
