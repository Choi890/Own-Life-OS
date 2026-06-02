package com.ownlifeos.domain.model

enum class RecommendationSurface(val label: String) {
    DAILY_STRATEGY("오늘의 운영 전략"),
    TASK_KILL("Task Kill"),
    FORECAST("Future Load Forecast"),
    SIMULATION("Life Simulation")
}

enum class RecommendationFeedbackType(val label: String) {
    MATCHED("맞음"),
    TOO_CONSERVATIVE("너무 보수적임"),
    TOO_AGGRESSIVE("너무 빡셈"),
    NOT_CONTEXTUAL("상황과 안 맞음")
}

data class RecommendationFeedback(
    val id: Long,
    val date: String,
    val surface: RecommendationSurface,
    val feedbackType: RecommendationFeedbackType,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
