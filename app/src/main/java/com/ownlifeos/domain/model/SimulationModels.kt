package com.ownlifeos.domain.model

enum class PossibilityLevel(val label: String) {
    LOW("낮음"),
    MEDIUM("중간"),
    HIGH("높음")
}

data class LifeSimulationInput(
    val title: String,
    val description: String,
    val expectedEnergyCost: Int,
    val expectedDurationMinutes: Int,
    val urgency: Int,
    val reversibility: Int,
    val importance: Int
)

data class LifeSimulationResult(
    val date: String,
    val title: String,
    val completionPossibility: PossibilityLevel,
    val stressImpact: PossibilityLevel,
    val regretPossibility: PossibilityLevel,
    val completionScore: Int,
    val stressScore: Int,
    val regretScore: Int,
    val summary: String,
    val reasons: List<Reason>,
    val generatedAt: Long = System.currentTimeMillis()
)
