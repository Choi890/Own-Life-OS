package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.WeeklyReportGenerator
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask

class GetWeeklySystemReportUseCase {
    fun execute(
        endDate: String,
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>
    ) = WeeklyReportGenerator.generate(
        endDate = endDate,
        checkIns = checkIns,
        tasks = tasks,
        reviews = reviews
    )
}
