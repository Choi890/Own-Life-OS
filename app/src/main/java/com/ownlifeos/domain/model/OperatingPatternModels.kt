package com.ownlifeos.domain.model

data class OperatingPatternReport(
    val startDate: String,
    val endDate: String,
    val goodTimeWindow: String,
    val riskTimeWindow: String,
    val failureCondition: String,
    val recoveryActions: List<String>,
    val feedbackSummary: String,
    val reasons: List<Reason>
)
