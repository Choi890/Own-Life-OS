package com.ownlifeos.domain.model

enum class RebalanceAction(val label: String) {
    DO_NOW("지금 처리"),
    DO_AFTER_RECOVERY("회복 후 처리"),
    DEFER("뒤로 미루기"),
    SPLIT("작게 나누기"),
    DROP_IF_OPTIONAL("선택 작업이면 제외")
}

data class RebalancedTask(
    val task: DailyTask,
    val action: RebalanceAction,
    val score: Int,
    val reasons: List<Reason>
)

data class RebalancedDayPlan(
    val date: String,
    val nowTasks: List<RebalancedTask>,
    val laterTasks: List<RebalancedTask>,
    val deferCandidates: List<RebalancedTask>,
    val quickWinTasks: List<RebalancedTask>,
    val reasons: List<Reason>,
    val generatedAt: Long = System.currentTimeMillis()
)
