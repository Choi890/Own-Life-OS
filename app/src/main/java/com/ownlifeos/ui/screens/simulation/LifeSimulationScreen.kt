package com.ownlifeos.ui.screens.simulation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FilterChip
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
import com.ownlifeos.domain.model.LifeSimulationResult
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.LevelSelector
import com.ownlifeos.ui.components.OwnLifeCard
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import kotlinx.coroutines.delay

@Composable
fun LifeSimulationScreen(
    uiState: LifeSimulationUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onDurationChange: (String) -> Unit,
    onUrgencyChange: (Int) -> Unit,
    onReversibilityChange: (Int) -> Unit,
    onImportanceChange: (Int) -> Unit,
    onCheck: () -> Unit,
    onSave: () -> Unit
) {
    val motionEnabled = rememberMotionEnabled()
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(uiState.result?.generatedAt) {
        if (uiState.result != null) {
            delay(180)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = ownLifeEnterTransition(motionEnabled),
        exit = ownLifeExitTransition(motionEnabled)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header()
            SimulationForm(
                form = uiState.form,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onEnergyChange = onEnergyChange,
                onDurationChange = onDurationChange,
                onUrgencyChange = onUrgencyChange,
                onReversibilityChange = onReversibilityChange,
                onImportanceChange = onImportanceChange,
                onCheck = onCheck
            )
            uiState.result?.let { SimulationResultCard(it, onSave) }
            uiState.form.savedMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun Header() {
    OwnLifeCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Text("이 선택 괜찮을까?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = "지금 하려는 일이 오늘 상태에 맞는지 확인합니다.",
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.78f)
        )
        Text(
            text = "Life Simulation",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.62f)
        )
    }
}

@Composable
private fun SimulationForm(
    form: LifeSimulationFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onDurationChange: (String) -> Unit,
    onUrgencyChange: (Int) -> Unit,
    onReversibilityChange: (Int) -> Unit,
    onImportanceChange: (Int) -> Unit,
    onCheck: () -> Unit
) {
    var showAdvanced by remember { mutableStateOf(false) }

    OwnLifeCard(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = form.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("무엇을 할까요?") },
            singleLine = true
        )
        SimulationBurdenSelector(
            selectedEnergy = form.expectedEnergyCost,
            onEnergyChange = onEnergyChange
        )
        FluidButton(
            text = "선택 확인하기",
            onClick = onCheck,
            enabled = form.title.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.PlayArrow,
            expandContent = true
        )
        SimulationExampleChips(
            onPick = { title, energy ->
                onTitleChange(title)
                onEnergyChange(energy)
            }
        )
        FluidButton(
            text = if (showAdvanced) "고급 설정 접기" else "시간·긴급도 직접 설정",
            onClick = { showAdvanced = !showAdvanced },
            modifier = Modifier.fillMaxWidth(),
            style = FluidButtonStyle.Outlined,
            expandContent = true
        )
        AnimatedVisibility(visible = showAdvanced) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = form.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("설명") },
                    minLines = 2,
                    maxLines = 5
                )
                OutlinedTextField(
                    value = form.expectedDurationMinutes.toString(),
                    onValueChange = onDurationChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("예상 시간(분)") },
                    singleLine = true
                )
                LevelSelector("예상 에너지", form.expectedEnergyCost, listOf("1", "2", "3", "4", "5"), onEnergyChange)
                LevelSelector("긴급도", form.urgency, listOf("1", "2", "3", "4", "5"), onUrgencyChange)
                LevelSelector("되돌리기 쉬움", form.reversibility, listOf("1", "2", "3", "4", "5"), onReversibilityChange)
                LevelSelector("중요도", form.importance, listOf("1", "2", "3", "4", "5"), onImportanceChange)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimulationBurdenSelector(
    selectedEnergy: Int,
    onEnergyChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "얼마나 부담되나요?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                1 to "가벼움",
                3 to "보통",
                5 to "무거움"
            ).forEach { (value, label) ->
                FilterChip(
                    selected = selectedEnergy.toBurdenValue() == value,
                    onClick = { onEnergyChange(value) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimulationExampleChips(
    onPick: (String, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "예시",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "새 작업 시작" to 5,
                "야식 먹기" to 3,
                "쇼츠 보기" to 3,
                "지금 자기" to 1
            ).forEach { (title, energy) ->
                FilterChip(
                    selected = false,
                    onClick = { onPick(title, energy) },
                    label = { Text(title) }
                )
            }
        }
    }
}

@Composable
private fun SimulationResultCard(
    result: LifeSimulationResult,
    onSave: () -> Unit
) {
    val judgement = when {
        result.stressScore >= 70 || result.regretScore >= 70 -> "지금은 보류 추천"
        result.completionScore >= 65 && result.stressScore < 60 -> "지금 해도 괜찮습니다"
        else -> "작게 줄이면 괜찮습니다"
    }
    val alternative = when (judgement) {
        "지금은 보류 추천" -> "내일 오전 첫 작업으로 넘기거나, 오늘은 10분 준비만 추천합니다."
        "작게 줄이면 괜찮습니다" -> "15~25분 안에 끝나는 범위로 줄여서 시작하세요."
        else -> "바로 시작하되, 15분 안에 끝나는 방식이 좋습니다."
    }

    OwnLifeCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        SectionHeader(title = "판단 결과", trailing = result.title)
        Text(
            text = judgement,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = result.summary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "완료 가능성 ${result.completionPossibility.label} · 스트레스 영향 ${result.stressImpact.label} · 후회 가능성 ${result.regretPossibility.label}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
        )
        Text(
            text = "이유",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        ReasonList(reasons = result.reasons, limit = 3)
        Text(
            text = "대안",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = alternative,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "행동 버튼",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        FluidButton(
            text = "판단 저장",
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Save,
            expandContent = true
        )
    }
}

private fun Int.toBurdenValue(): Int = when {
    this <= 2 -> 1
    this >= 4 -> 5
    else -> 3
}
