package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RecommendedMode
import com.ownlifeos.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LifeMetricsCalculatorTest {
    @Test
    fun lowBatteryAndHighStressRecommendRecovery() {
        val metrics = LifeMetricsCalculator.calculate(
            checkIn = DailyCheckIn(
                id = 1,
                date = "2026-06-01",
                sleepHours = 3.5,
                mood = 1,
                bodyCondition = 1,
                burdenLevel = 5,
                memo = ""
            ),
            tasks = listOf(
                DailyTask(1, "2026-06-01", "무거운 발표 준비", 5, 5, TaskStatus.WAITING),
                DailyTask(2, "2026-06-01", "긴 문서 작성", 4, 4, TaskStatus.IN_PROGRESS)
            ),
            review = null
        )

        assertEquals(RecommendedMode.RECOVERY, metrics.recommendedMode)
        assertTrue(metrics.lifeBattery <= 35)
        assertTrue(metrics.stressLevel >= 70)
    }

    @Test
    fun highFocusWithImportantPendingTaskRecommendDeepFocus() {
        val metrics = LifeMetricsCalculator.calculate(
            checkIn = DailyCheckIn(
                id = 1,
                date = "2026-06-01",
                sleepHours = 8.0,
                mood = 5,
                bodyCondition = 5,
                burdenLevel = 1,
                memo = ""
            ),
            tasks = listOf(
                DailyTask(1, "2026-06-01", "핵심 기획 작성", 5, 2, TaskStatus.WAITING)
            ),
            review = null
        )

        assertEquals(RecommendedMode.DEEP_FOCUS, metrics.recommendedMode)
        assertTrue(metrics.focusLevel >= 58)
    }
}
