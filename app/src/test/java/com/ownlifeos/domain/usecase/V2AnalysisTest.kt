package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.AnalysisInputs
import com.ownlifeos.domain.analysis.LifeBatteryAnalyzer
import com.ownlifeos.domain.analysis.TaskQueueRanker
import com.ownlifeos.domain.analysis.TodaySystemAnalyzer
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TodayMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class V2AnalysisTest {
    @Test
    fun overloadedDayRecommendsEmergencyModeWithReasons() {
        val inputs = AnalysisInputs(
            date = "2026-06-01",
            todayCheckIn = DailyCheckIn(
                id = 1,
                date = "2026-06-01",
                sleepHours = 4.5,
                mood = 2,
                bodyCondition = 2,
                burdenLevel = 5,
                memo = ""
            ),
            todayTasks = (1..8).map {
                DailyTask(
                    id = it.toLong(),
                    date = "2026-06-01",
                    title = "작업 $it",
                    importance = 4,
                    energyCost = 4,
                    status = TaskStatus.WAITING,
                    focusNeed = 4
                )
            },
            todayReview = null,
            recentCheckIns = emptyList(),
            recentTasks = emptyList(),
            recentReviews = emptyList()
        )

        val analysis = TodaySystemAnalyzer.analyze(inputs)

        assertEquals(TodayMode.EMERGENCY, analysis.mode)
        assertTrue(analysis.modeReasons.isNotEmpty())
        assertTrue(analysis.batteryFactors.any { it.points < 0 })
        assertTrue(analysis.errorSignals.isNotEmpty())
    }

    @Test
    fun taskRankerPrioritizesDeadlineAndDeferredCount() {
        val battery = LifeBatteryAnalyzer.analyze(
            AnalysisInputs(
                date = "2026-06-01",
                todayCheckIn = DailyCheckIn(1, "2026-06-01", 7.5, 4, 4, 2, ""),
                todayTasks = emptyList(),
                todayReview = null,
                recentCheckIns = emptyList(),
                recentTasks = emptyList(),
                recentReviews = emptyList()
            )
        )

        val tasks = listOf(
            DailyTask(
                id = 1,
                date = "2026-06-01",
                title = "낮은 우선순위",
                importance = 2,
                energyCost = 2,
                status = TaskStatus.WAITING,
                deadlineDate = "2026-06-10"
            ),
            DailyTask(
                id = 2,
                date = "2026-06-01",
                title = "오늘 마감",
                importance = 4,
                energyCost = 3,
                status = TaskStatus.WAITING,
                deadlineDate = "2026-06-01",
                deferredCount = 2
            )
        )

        val ranked = TaskQueueRanker.rank(tasks, battery, "2026-06-01")

        assertEquals("오늘 마감", ranked.first().task.title)
        assertTrue(ranked.first().reasons.isNotEmpty())
    }
}
