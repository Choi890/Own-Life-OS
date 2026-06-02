package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.prediction.DayRebalancer

class GetRebalancedDayPlanUseCase {
    fun execute(
        date: String,
        tasks: List<DailyTask>,
        analysis: TodaySystemAnalysis
    ) = DayRebalancer.rebalance(date, tasks, analysis)
}
