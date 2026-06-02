package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.prediction.SystemHealthAnalyzer

class GetSystemHealthReportV3UseCase {
    fun execute(
        endDate: String,
        checkIns: List<DailyCheckIn>,
        tasks: List<DailyTask>,
        reviews: List<DailyReview>
    ) = SystemHealthAnalyzer.analyze(endDate, checkIns, tasks, reviews)
}
