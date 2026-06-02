package com.ownlifeos.data.local.entity

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.DecisionRecord
import com.ownlifeos.domain.model.DecisionRiskLevel

fun CheckInEntity.toDomain(): DailyCheckIn = DailyCheckIn(
    id = id,
    date = date,
    sleepHours = sleepHours,
    mood = mood,
    bodyCondition = bodyCondition,
    burdenLevel = burdenLevel,
    memo = memo
)

fun LifeTaskEntity.toDomain(): DailyTask = DailyTask(
    id = id,
    date = date,
    title = title,
    importance = importance,
    energyCost = energyCost,
    status = status,
    deadlineDate = deadlineDate,
    focusNeed = focusNeed,
    deferredCount = deferredCount,
    startedAt = startedAt,
    completedAt = completedAt,
    lastDeferredAt = lastDeferredAt
)

fun ReviewEntity.toDomain(): DailyReview = DailyReview(
    id = id,
    date = date,
    goodThings = goodThings,
    errorLogs = errorLogs,
    carryOver = carryOver
)

fun DecisionEntity.toDomain(): DecisionRecord = DecisionRecord(
    id = id,
    date = date,
    title = title,
    description = description,
    expectedEnergyCost = expectedEnergyCost,
    urgency = urgency,
    reversibility = reversibility,
    importance = importance,
    predictedRiskScore = predictedRiskScore,
    predictedRiskLevel = runCatching {
        DecisionRiskLevel.valueOf(predictedRiskLevel)
    }.getOrDefault(DecisionRiskLevel.MEDIUM),
    reasons = reasons,
    createdAt = createdAt
)
