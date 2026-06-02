package com.ownlifeos.domain.model

enum class TodayMode(
    val title: String,
    val description: String
) {
    PERFORMANCE(
        title = "Performance Mode",
        description = "처리량을 높여도 되는 날입니다."
    ),
    BALANCED(
        title = "Balanced Mode",
        description = "계획한 루틴을 균형 있게 유지하세요."
    ),
    LOW_POWER(
        title = "Low Power Mode",
        description = "낮은 에너지 작업과 정리를 우선하세요."
    ),
    RECOVERY(
        title = "Recovery Mode",
        description = "회복과 부담 축소가 우선입니다."
    ),
    FOCUS(
        title = "Focus Mode",
        description = "가장 중요한 작업 하나에 집중하기 좋은 상태입니다."
    ),
    MAINTENANCE(
        title = "Maintenance Mode",
        description = "새 작업보다 정리, 유지, 마감 관리에 적합합니다."
    ),
    EMERGENCY(
        title = "Emergency Mode",
        description = "작업 과부하 신호가 강합니다. 오늘의 범위를 줄이세요."
    )
}

enum class ReasonImpact {
    LOW,
    MEDIUM,
    HIGH
}

enum class ReasonSource {
    SLEEP,
    MOOD,
    BODY,
    STRESS,
    TASK,
    HISTORY,
    REVIEW,
    DECISION
}

data class Reason(
    val title: String,
    val detail: String,
    val impact: ReasonImpact,
    val source: ReasonSource
)

data class BatteryFactor(
    val title: String,
    val detail: String,
    val points: Int
) {
    val isRecovery: Boolean
        get() = points > 0
}

enum class ErrorSignalType(val label: String) {
    SLEEP_SHORTAGE("수면 부족"),
    TASK_OVERLOAD("작업 과부하"),
    CARRY_OVER_ACCUMULATION("미완료 작업 누적"),
    LATE_TASK_START("늦은 시간 새 작업 시작"),
    REPEATED_FAILURE_PATTERN("반복 실패 패턴"),
    FATIGUE_ACCUMULATION("피로 누적")
}

data class ErrorSignal(
    val type: ErrorSignalType,
    val severity: ReasonImpact,
    val title: String,
    val detail: String,
    val reasons: List<Reason>
)

data class TodaySystemAnalysis(
    val date: String,
    val lifeBattery: Int,
    val focusLevel: Int,
    val stressLoad: Int,
    val fatigueLoad: Int,
    val mode: TodayMode,
    val modeReasons: List<Reason>,
    val batteryReasons: List<Reason>,
    val batteryFactors: List<BatteryFactor> = emptyList(),
    val errorSignals: List<ErrorSignal>,
    val generatedAt: Long = System.currentTimeMillis()
)

data class RankedTask(
    val task: DailyTask,
    val score: Int,
    val reasons: List<Reason>
)

data class WeeklySystemReport(
    val startDate: String,
    val endDate: String,
    val averageLifeBattery: Int,
    val averageFocusLevel: Int,
    val averageStressLoad: Int,
    val bestDay: String?,
    val riskyDay: String?,
    val repeatedErrors: List<String>,
    val nextWeekStrategies: List<String>,
    val reasons: List<Reason>
)

enum class DecisionRiskLevel(val label: String) {
    LOW("낮음"),
    MEDIUM("중간"),
    HIGH("높음")
}

data class DecisionPrediction(
    val title: String,
    val riskScore: Int,
    val riskLevel: DecisionRiskLevel,
    val reasons: List<Reason>,
    val recommendation: String
)
