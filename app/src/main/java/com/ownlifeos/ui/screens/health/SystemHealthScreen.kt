package com.ownlifeos.ui.screens.health

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.ownlifeos.domain.model.SystemHealthReport
import com.ownlifeos.ui.components.MetricCard
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import com.ownlifeos.util.DateUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety

@Composable
fun SystemHealthScreen(uiState: SystemHealthUiState) {
    val motionEnabled = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
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
                Text("System Health를 계산하는 중입니다.")
            } else {
                Header(report)
                MetricCard(
                    title = "System Health",
                    value = report.healthScore,
                    supportingText = "배터리 안정성, 스트레스 변동, 미완료 누적, 회고 로그 기반",
                    icon = Icons.Outlined.HealthAndSafety,
                    color = MaterialTheme.colorScheme.primary,
                    motionEnabled = motionEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
                ListCard("안정적인 영역", report.stableAreas)
                ListCard("불안정한 영역", report.unstableAreas)
                ListCard("다음 주 전략", report.nextWeekStrategies)
                ReasonList(reasons = report.reasons, limit = 4)
            }
        }
    }
}

@Composable
private fun Header(report: SystemHealthReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("System Health Score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "${DateUtils.displayDate(report.weekStartDate)} - ${DateUtils.displayDate(report.weekEndDate)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ListCard(title: String, items: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = title)
            items.forEach { Text("- $it") }
        }
    }
}
