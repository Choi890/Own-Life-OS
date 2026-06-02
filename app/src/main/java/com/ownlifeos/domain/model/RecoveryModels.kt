package com.ownlifeos.domain.model

enum class RecoveryActionType(val label: String) {
    REST("휴식"),
    CLEANUP("정리"),
    HYDRATION("수분"),
    WALK("걷기"),
    EASY_TASK("쉬운 작업")
}

data class RecoveryAction(
    val title: String,
    val durationMinutes: Int,
    val type: RecoveryActionType,
    val instruction: String,
    val reason: Reason
)

data class RecoveryPlan(
    val date: String,
    val triggerLevel: ForecastRiskLevel,
    val title: String,
    val actions: List<RecoveryAction>,
    val reasons: List<Reason>,
    val generatedAt: Long = System.currentTimeMillis()
)
