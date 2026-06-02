package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.RecoveryPlanDao
import com.ownlifeos.data.local.entity.RecoveryPlanEntity
import com.ownlifeos.domain.model.RecoveryPlan

class RecoveryPlanRepository(
    private val dao: RecoveryPlanDao
) {
    suspend fun save(plan: RecoveryPlan) {
        dao.insert(
            RecoveryPlanEntity(
                date = plan.date,
                triggerLevel = plan.triggerLevel.name,
                title = plan.title,
                actionsJson = plan.actions.joinToString(separator = "\n") {
                    "${it.title} (${it.durationMinutes}분): ${it.instruction}"
                },
                reasonsJson = plan.reasons.joinToString(separator = "\n") {
                    "${it.title}: ${it.detail}"
                },
                createdAt = plan.generatedAt
            )
        )
    }
}
