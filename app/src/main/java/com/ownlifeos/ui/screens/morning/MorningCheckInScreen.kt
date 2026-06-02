package com.ownlifeos.ui.screens.morning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.OwnLifeCard
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import com.ownlifeos.util.DateUtils

@Composable
fun MorningCheckInScreen(
    uiState: MorningCheckInUiState,
    onSleepChange: (Double) -> Unit,
    onMoodChange: (Int) -> Unit,
    onBodyChange: (Int) -> Unit,
    onBurdenChange: (Int) -> Unit,
    onMemoChange: (String) -> Unit,
    onOpenHome: () -> Unit,
    onSave: () -> Unit
) {
    val motionEnabled = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
    var showOptional by remember { mutableStateOf(false) }
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
            OwnLifeCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = "30초 아침 체크인",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "오늘 상태를 빠르게 확인하고 운영 카드를 만듭니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f)
                )
                Text(
                    text = "${DateUtils.displayDate(uiState.date)} · Morning Check-in",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.62f)
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "기본값은 최근 기록을 기준으로 자동 선택되었습니다. 필요한 부분만 바꾸면 됩니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.autoFilled) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "자동 추정값을 적용했습니다.",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "바뀐 것만 수정하고 그대로 시작할 수 있습니다.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            uiState.estimateReasons.take(2).forEach { reason ->
                                Text(
                                    text = "- $reason",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    CompactChoiceRow(
                        title = "몸 상태",
                        selectedValue = uiState.bodyCondition,
                        options = listOf(
                            CheckInOption(value = 5, label = "좋음"),
                            CheckInOption(value = 3, label = "보통"),
                            CheckInOption(value = 1, label = "무거움")
                        ),
                        onValueChange = {
                            onBodyChange(it)
                            onMoodChange(it)
                        }
                    )

                    CompactChoiceRow(
                        title = "오늘 부담",
                        selectedValue = uiState.burdenLevel,
                        options = listOf(
                            CheckInOption(value = 1, label = "낮음"),
                            CheckInOption(value = 3, label = "보통"),
                            CheckInOption(value = 5, label = "높음")
                        ),
                        onValueChange = onBurdenChange
                    )

                    OutlinedTextField(
                        value = uiState.memo,
                        onValueChange = onMemoChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("오늘 가장 중요한 일 1개") },
                        singleLine = true
                    )

                    FluidButton(
                        text = if (showOptional) "추가 기록 접기" else "수면/기분 추가 기록",
                        onClick = { showOptional = !showOptional },
                        modifier = Modifier.fillMaxWidth(),
                        style = FluidButtonStyle.Outlined,
                        expandContent = true
                    )

                    AnimatedVisibility(visible = showOptional) {
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            SleepChoiceRow(
                                sleepHours = uiState.sleepHours,
                                onSleepChange = onSleepChange
                            )

                            CompactChoiceRow(
                                title = "기분",
                                selectedValue = uiState.mood,
                                options = listOf(
                                    CheckInOption(value = 5, label = "좋음"),
                                    CheckInOption(value = 3, label = "보통"),
                                    CheckInOption(value = 1, label = "무거움")
                                ),
                                onValueChange = onMoodChange
                            )
                        }
                    }

                    FluidButton(
                        text = "오늘 전략 만들기",
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.Save,
                        expandContent = true
                    )
                }
            }

            AnimatedVisibility(visible = uiState.saved) {
                OwnLifeCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = "체크인 반영 완료",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "오늘 상태를 반영해 운영 카드를 다시 정리했습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                    )
                    FluidButton(
                        text = "홈에서 운영 카드 보기",
                        onClick = onOpenHome,
                        modifier = Modifier.fillMaxWidth(),
                        expandContent = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

private data class CheckInOption(
    val value: Int,
    val label: String
)

private data class SleepOption(
    val hours: Double,
    val label: String,
    val detail: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompactChoiceRow(
    title: String,
    selectedValue: Int,
    options: List<CheckInOption>,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedValue.toCompactChoice() == option.value,
                    onClick = { onValueChange(option.value) },
                    label = { Text(option.label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SleepChoiceRow(
    sleepHours: Double,
    onSleepChange: (Double) -> Unit
) {
    val options = listOf(
        SleepOption(hours = 5.0, label = "부족", detail = "6시간 미만"),
        SleepOption(hours = 7.0, label = "보통", detail = "6~8시간"),
        SleepOption(hours = 8.5, label = "충분", detail = "8시간 이상")
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "수면",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedSleepLabel(sleepHours) == option.label,
                    onClick = { onSleepChange(option.hours) },
                    label = {
                        Text("${option.label} · ${option.detail}")
                    }
                )
            }
        }
    }
}

private fun selectedSleepLabel(sleepHours: Double): String = when {
    sleepHours < 6.0 -> "부족"
    sleepHours < 8.0 -> "보통"
    else -> "충분"
}

private fun Int.toCompactChoice(): Int = when {
    this <= 2 -> 1
    this >= 4 -> 5
    else -> 3
}
