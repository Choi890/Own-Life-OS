package com.ownlifeos.ui.screens.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.ownlifeos.domain.model.DecisionPrediction
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.LevelSelector
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled

@Composable
fun DecisionCheckScreen(
    uiState: DecisionCheckUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onUrgencyChange: (Int) -> Unit,
    onReversibilityChange: (Int) -> Unit,
    onImportanceChange: (Int) -> Unit,
    onSave: () -> Unit
) {
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
            DecisionForm(
                form = uiState.form,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onEnergyChange = onEnergyChange,
                onUrgencyChange = onUrgencyChange,
                onReversibilityChange = onReversibilityChange,
                onImportanceChange = onImportanceChange
            )
            uiState.prediction?.let {
                PredictionCard(prediction = it, onSave = onSave)
            }
            uiState.form.savedMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            RecentDecisionList(uiState)
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
                text = "Decision Check",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "현재 상태 기준으로 선택의 후회 가능성을 로컬 규칙으로 계산합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DecisionForm(
    form: DecisionFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onUrgencyChange: (Int) -> Unit,
    onReversibilityChange: (Int) -> Unit,
    onImportanceChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = form.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("선택") },
                singleLine = true
            )
            OutlinedTextField(
                value = form.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("메모") },
                minLines = 2,
                maxLines = 5
            )
            LevelSelector("예상 에너지", form.expectedEnergyCost, listOf("1", "2", "3", "4", "5"), onEnergyChange)
            LevelSelector("긴급도", form.urgency, listOf("1", "2", "3", "4", "5"), onUrgencyChange)
            LevelSelector("되돌리기 쉬움", form.reversibility, listOf("1", "2", "3", "4", "5"), onReversibilityChange)
            LevelSelector("중요도", form.importance, listOf("1", "2", "3", "4", "5"), onImportanceChange)
        }
    }
}

@Composable
private fun PredictionCard(
    prediction: DecisionPrediction,
    onSave: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = "Regret Predictor", trailing = "위험도 ${prediction.riskScore}")
            Text(
                text = "후회 가능성: ${prediction.riskLevel.label}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = prediction.recommendation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ReasonList(reasons = prediction.reasons, limit = 4)
            FluidButton(
                text = "선택 기록 저장",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Save,
                expandContent = true
            )
        }
    }
}

@Composable
private fun RecentDecisionList(uiState: DecisionCheckUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "최근 선택 기록")
            if (uiState.recentDecisions.isEmpty()) {
                Text(
                    text = "아직 저장된 선택 기록이 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                uiState.recentDecisions.take(5).forEach {
                    Text(
                        text = "${it.title} · ${it.predictedRiskLevel.label} ${it.predictedRiskScore}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}
