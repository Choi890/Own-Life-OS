package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.BatteryAnalysis
import com.ownlifeos.domain.analysis.TaskQueueRanker
import com.ownlifeos.domain.model.DailyTask

class GetRankedTaskQueueUseCase {
    fun execute(
        tasks: List<DailyTask>,
        batteryAnalysis: BatteryAnalysis,
        today: String
    ) = TaskQueueRanker.rank(tasks, batteryAnalysis, today)
}
