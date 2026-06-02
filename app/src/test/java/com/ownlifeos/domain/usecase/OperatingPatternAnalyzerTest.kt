package com.ownlifeos.domain.usecase

import com.ownlifeos.domain.analysis.OperatingPatternAnalyzer
import com.ownlifeos.domain.model.DailyCheckIn
import com.ownlifeos.domain.model.DailyReview
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.RecommendationSurface
import com.ownlifeos.domain.model.TaskStatus
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class OperatingPatternAnalyzerTest {
    @Test
    fun patternReportUsesLocalRecordsAndFeedback() {
        val completedAt = LocalDateTime
            .of(2026, 6, 1, 10, 30)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val report = OperatingPatternAnalyzer.analyze(
            startDate = "2026-05-19",
            endDate = "2026-06-01",
            checkIns = listOf(
                DailyCheckIn(1, "2026-05-31", 5.0, 2, 2, 5, ""),
                DailyCheckIn(2, "2026-06-01", 5.5, 3, 3, 4, "")
            ),
            tasks = listOf(
                DailyTask(1, "2026-06-01", "핵심 작업", 5, 3, TaskStatus.DONE, completedAt = completedAt),
                DailyTask(2, "2026-06-01", "남은 작업", 3, 4, TaskStatus.WAITING)
            ),
            reviews = listOf(DailyReview(1, "2026-05-31", "", "피로 누적", "")),
            feedbacks = listOf(
                RecommendationFeedback(
                    id = 1,
                    date = "2026-06-01",
                    surface = RecommendationSurface.DAILY_STRATEGY,
                    feedbackType = RecommendationFeedbackType.MATCHED
                )
            )
        )

        assertTrue(report.goodTimeWindow.contains("오전"))
        assertTrue(report.failureCondition.contains("수면"))
        assertTrue(report.feedbackSummary.contains("피드백"))
        assertTrue(report.reasons.isNotEmpty())
    }
}
