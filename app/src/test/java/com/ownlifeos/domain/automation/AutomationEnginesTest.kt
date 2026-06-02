package com.ownlifeos.domain.automation

import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomationEnginesTest {
    @Test
    fun taskBurdenEstimatorUsesKeywordRules() {
        assertEquals("무거움", TaskBurdenEstimator.estimate("앱 기능 구현").label)
        assertEquals("가벼움", TaskBurdenEstimator.estimate("교수님께 답장").label)
        assertEquals("보통", TaskBurdenEstimator.estimate("자료 정리").label)
    }

    @Test
    fun checkInEstimatorRaisesBurdenForLargeQueueAndFatigue() {
        val estimate = CheckInEstimator.estimate(
            date = "2026-06-01",
            recentCheckIns = emptyList(),
            todayTasks = listOf(
                DailyTask(1, "2026-06-01", "구현", 4, 5, TaskStatus.WAITING),
                DailyTask(2, "2026-06-01", "보고서", 4, 5, TaskStatus.WAITING),
                DailyTask(3, "2026-06-01", "답장", 2, 1, TaskStatus.WAITING)
            ),
            recentTasks = listOf(
                DailyTask(4, "2026-05-31", "미완료", 3, 3, TaskStatus.WAITING),
                DailyTask(5, "2026-05-30", "미완료 2", 3, 3, TaskStatus.WAITING)
            ),
            recentReviews = listOf(DailyReview(1, "2026-05-31", "", "피로 누적", ""))
        )

        assertTrue(estimate.burdenLevel >= 4)
        assertTrue(estimate.reasons.any { it.contains("미완료") })
    }

    @Test
    fun reviewDraftSummarizesCompletedAndPendingTasks() {
        val draft = ReviewDraftGenerator.generate(
            listOf(
                DailyTask(1, "2026-06-01", "답장", 2, 1, TaskStatus.DONE),
                DailyTask(2, "2026-06-01", "프로젝트 구현", 4, 5, TaskStatus.WAITING)
            )
        )

        assertTrue(draft.goodThings.contains("1개"))
        assertTrue(draft.carryOver.contains("프로젝트 구현"))
    }
}
