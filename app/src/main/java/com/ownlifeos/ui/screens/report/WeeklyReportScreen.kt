package com.ownlifeos.ui.screens.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ownlifeos.domain.model.WeeklySystemReport
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import com.ownlifeos.util.DateUtils

@Composable
fun WeeklyReportScreen(uiState: WeeklyReportUiState) {
    val motionEnabled = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = ownLifeEnterTransition(motionEnabled),
        exit = ownLifeExitTransition(motionEnabled)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val report = uiState.report
            if (report == null) {
                Text("주간 리포트를 계산하는 중입니다.")
            } else {
                ReportHeader(report)
                WeeklyOneScreenSummary(report)
                FluidButton(
                    text = if (showDetails) "세부 근거 접기" else "세부 근거 보기",
                    onClick = { showDetails = !showDetails },
                    modifier = Modifier.fillMaxWidth(),
                    style = FluidButtonStyle.Outlined,
                    expandContent = true
                )
                AnimatedVisibility(visible = showDetails) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        MetricSummary(report)
                        DaySummary(report)
                        RepeatedErrors(report)
                        StrategyList(report)
                        ReasonList(reasons = report.reasons, limit = 4)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportHeader(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "주간 시스템 요약",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${DateUtils.displayDate(report.startDate)} - ${DateUtils.displayDate(report.endDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Weekly System Report",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyOneScreenSummary(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = "이번 주 요약")
            Text(
                text = weeklyJudgement(report),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "평균: Battery ${report.averageLifeBattery} · Focus ${report.averageFocusLevel} · Stress ${report.averageStressLoad}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text("가장 좋았던 날: ${report.bestDay?.let { DateUtils.displayDate(it) } ?: "-"}")
            Text("가장 위험했던 날: ${report.riskyDay?.let { DateUtils.displayDate(it) } ?: "-"}")
            val repeated = report.repeatedErrors.firstOrNull() ?: "강한 반복 오류 없음"
            Text("반복 신호: $repeated")
            Text(
                text = "다음 주 전략: ${report.nextWeekStrategies.firstOrNull() ?: "작업량을 작게 유지"}",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MetricSummary(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(title = "이번 주 평균")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReportMetric("Battery", report.averageLifeBattery)
                ReportMetric("Focus", report.averageFocusLevel)
                ReportMetric("Stress", report.averageStressLoad)
            }
        }
    }
}

private fun weeklyJudgement(report: WeeklySystemReport): String = when {
    report.averageStressLoad >= 70 -> "판정: 작업 과부하 주의"
    report.averageLifeBattery <= 45 -> "판정: 회복 우선"
    report.averageFocusLevel >= 70 && report.averageStressLoad < 60 -> "판정: 안정적 집중 가능"
    else -> "판정: 보통 운영"
}

@Composable
private fun ReportMetric(label: String, value: Int) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DaySummary(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "주간 포인트")
            Text("가장 좋았던 날: ${report.bestDay?.let { DateUtils.displayDate(it) } ?: "-"}")
            Text("가장 위험했던 날: ${report.riskyDay?.let { DateUtils.displayDate(it) } ?: "-"}")
        }
    }
}

@Composable
private fun RepeatedErrors(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "반복 오류")
            if (report.repeatedErrors.isEmpty()) {
                Text(
                    text = "반복 감지된 오류가 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                report.repeatedErrors.forEach { Text("- $it") }
            }
        }
    }
}

@Composable
private fun StrategyList(report: WeeklySystemReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "다음 주 추천 전략")
            report.nextWeekStrategies.forEach { Text("- $it") }
        }
    }
}
