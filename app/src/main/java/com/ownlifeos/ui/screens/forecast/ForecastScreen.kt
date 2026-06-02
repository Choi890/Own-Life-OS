package com.ownlifeos.ui.screens.forecast

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
import com.ownlifeos.domain.model.ForecastResult
import com.ownlifeos.domain.model.RebalancedDayPlan
import com.ownlifeos.domain.model.RecoveryAction
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled

@Composable
fun ForecastScreen(uiState: ForecastUiState) {
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
            Header()
            uiState.forecast?.results.orEmpty().forEach { ForecastCard(it) }
            RebalancePreview(uiState.rebalancedPlan)
            RecoveryPreview(uiState.recoveryPlan?.actions.orEmpty())
        }
    }
}

@Composable
private fun Header() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Future Load Forecast",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "오늘 감당 가능한 범위와 작업 순서를 로컬 규칙으로 계산합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ForecastCard(result: ForecastResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = result.timeBlock.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${result.riskLevel.label} · ${result.riskScore}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(result.summary)
            ReasonList(reasons = result.reasons, limit = 3)
        }
    }
}

@Composable
private fun RebalancePreview(plan: RebalancedDayPlan?) {
    val nowTasks = plan?.nowTasks.orEmpty()
    val deferCandidates = plan?.deferCandidates.orEmpty()
    val quickWins = plan?.quickWinTasks.orEmpty()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Auto Day Rebalancer")
            if (nowTasks.isEmpty()) {
                Text("지금 처리할 추천 작업이 없습니다.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text("지금 먼저", fontWeight = FontWeight.SemiBold)
                nowTasks.forEach { Text("- ${it.task.title} · ${it.action.label}") }
            }
            if (deferCandidates.isNotEmpty()) {
                Text("오늘 보류 후보", fontWeight = FontWeight.SemiBold)
                deferCandidates.take(3).forEach {
                    Text(
                        text = "- ${it.task.title} · ${it.action.label}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (quickWins.isNotEmpty()) {
                Text("대신 처리하기 쉬운 작업", fontWeight = FontWeight.SemiBold)
                quickWins.take(3).forEach {
                    Text(
                        text = "- ${it.task.title}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RecoveryPreview(actions: List<RecoveryAction>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Recovery Planner")
            actions.take(5).forEach {
                Text(
                    text = "- ${it.title} (${it.durationMinutes}분)",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = it.instruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
