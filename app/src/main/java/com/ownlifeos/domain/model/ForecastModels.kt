package com.ownlifeos.domain.model

enum class TimeBlock(val label: String) {
    MORNING("오전"),
    AFTERNOON("오후"),
    EVENING("저녁")
}

enum class ForecastRiskLevel(val label: String) {
    STABLE("Stable"),
    CAUTION("Caution"),
    HIGH_RISK("High Risk")
}

data class ForecastResult(
    val date: String,
    val timeBlock: TimeBlock,
    val riskLevel: ForecastRiskLevel,
    val riskScore: Int,
    val summary: String,
    val reasons: List<Reason>,
    val generatedAt: Long = System.currentTimeMillis()
)

data class FutureLoadForecast(
    val date: String,
    val results: List<ForecastResult>,
    val generatedAt: Long = System.currentTimeMillis()
) {
    val highestRisk: ForecastResult? = results.maxByOrNull { it.riskScore }
}
