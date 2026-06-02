package com.ownlifeos.domain.analysis

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask

data class AnalysisInputs(
    val date: String,
    val todayCheckIn: DailyCheckIn?,
    val todayTasks: List<DailyTask>,
    val todayReview: DailyReview?,
    val recentCheckIns: List<DailyCheckIn>,
    val recentTasks: List<DailyTask>,
    val recentReviews: List<DailyReview>
)
