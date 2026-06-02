package com.ownlifeos.domain.model

data class SystemHealthReport(
    val weekStartDate: String,
    val weekEndDate: String,
    val healthScore: Int,
    val stableAreas: List<String>,
    val unstableAreas: List<String>,
    val nextWeekStrategies: List<String>,
    val reasons: List<Reason>,
    val generatedAt: Long = System.currentTimeMillis()
)
