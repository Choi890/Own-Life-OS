package com.ownlifeos.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.Add
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
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RankedTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.LevelSelector
import com.ownlifeos.ui.components.OwnLifeCard
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.components.SignalChipRow
import com.ownlifeos.ui.components.StatusPill
import com.ownlifeos.ui.components.TaskQueueItem
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled

@Composable
fun TaskQueueScreen(
    tasks: List<DailyTask>,
    rankedTasks: List<RankedTask>,
    formState: TaskFormState,
    onTitleChange: (String) -> Unit,
    onImportanceChange: (Int) -> Unit,
    onEnergyChange: (Int) -> Unit,
    onFocusNeedChange: (Int) -> Unit,
    onDeadlineChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onStatusChange: (Long, TaskStatus) -> Unit,
    onDefer: (Long) -> Unit,
    onDelete: (DailyTask) -> Unit
) {
    val motionEnabled = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                StatusPill(
                    text = "Task Queue",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "할 일 정리하기",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "이름과 부담도만 입력하면 오늘 감당 가능한 순서로 재배치합니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SignalChipRow(
                    items = listOf(
                        "입력" to "간단",
                        "정렬" to "자동",
                        "저장" to "로컬"
                    ),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OwnLifeCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("할 일 이름") },
                        singleLine = true
                    )

                    TaskBurdenChoiceRow(
                        selectedEnergy = formState.energyCost,
                        estimateLabel = formState.burdenEstimateLabel,
                        estimateReason = formState.burdenEstimateReason,
                        autoEstimated = formState.burdenAutoEstimated,
                        onValueChange = { value ->
                            onEnergyChange(value)
                            onImportanceChange(value.toDefaultImportance())
                            onFocusNeedChange(value.toDefaultFocusNeed())
                        }
                    )

                    FluidButton(
                        text = if (showAdvanced) "정렬 기준 접기" else "정렬 기준 더 입력하기",
                        onClick = { showAdvanced = !showAdvanced },
                        modifier = Modifier.fillMaxWidth(),
                        style = FluidButtonStyle.Outlined,
                        expandContent = true
                    )

                    AnimatedVisibility(visible = showAdvanced) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            LevelSelector(
                                title = "중요도",
                                selectedValue = formState.importance,
                                labels = listOf("1", "2", "3", "4", "5"),
                                onValueChange = onImportanceChange
                            )

                            LevelSelector(
                                title = "에너지 소모량",
                                selectedValue = formState.energyCost,
                                labels = listOf("1", "2", "3", "4", "5"),
                                onValueChange = onEnergyChange
                            )

                            LevelSelector(
                                title = "집중 필요도",
                                selectedValue = formState.focusNeed,
                                labels = listOf("1", "2", "3", "4", "5"),
                                onValueChange = onFocusNeedChange
                            )

                            OutlinedTextField(
                                value = formState.deadlineDate,
                                onValueChange = onDeadlineChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("마감일 YYYY-MM-DD") },
                                singleLine = true
                            )
                        }
                    }

                    FluidButton(
                        text = "작업 추가",
                        onClick = onAddTask,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.Add,
                        expandContent = true
                    )

                    formState.message?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            SectionHeader(
                title = "오늘 상태에 맞게 정리한 순서",
                trailing = "${tasks.count { it.status == TaskStatus.DONE }} / ${tasks.size} 완료"
            )

            if (tasks.isEmpty()) {
                OwnLifeCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "아직 등록된 할 일이 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                rankedTasks.forEach { rankedTask ->
                    val task = rankedTask.task
                    TaskQueueItem(
                        task = task,
                        rankedTask = rankedTask,
                        onStatusChange = { onStatusChange(task.id, it) },
                        onDefer = { onDefer(task.id) },
                        onDelete = { onDelete(task) }
                    )
                }
                val doneTasks = tasks.filter { it.status == TaskStatus.DONE }
                if (doneTasks.isNotEmpty()) {
                    SectionHeader(title = "완료된 작업")
                    doneTasks.forEach { task ->
                        TaskQueueItem(
                            task = task,
                            onStatusChange = { onStatusChange(task.id, it) },
                            onDelete = { onDelete(task) }
                        )
                    }
                }
            }
        }
    }
}

private data class TaskBurdenOption(
    val value: Int,
    val label: String,
    val detail: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskBurdenChoiceRow(
    selectedEnergy: Int,
    estimateLabel: String,
    estimateReason: String,
    autoEstimated: Boolean,
    onValueChange: (Int) -> Unit
) {
    val options = listOf(
        TaskBurdenOption(value = 1, label = "가벼움", detail = "금방 끝남"),
        TaskBurdenOption(value = 3, label = "보통", detail = "일반 작업"),
        TaskBurdenOption(value = 5, label = "무거움", detail = "부담 큼")
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "이 일은 얼마나 부담되나요?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "부담도: $estimateLabel${if (autoEstimated) "으로 추정됨" else "으로 설정됨"}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = estimateReason,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedEnergy.toBurdenValue() == option.value,
                    onClick = { onValueChange(option.value) },
                    label = { Text("${option.label} · ${option.detail}") }
                )
            }
        }
    }
}

private fun Int.toBurdenValue(): Int = when {
    this <= 2 -> 1
    this >= 4 -> 5
    else -> 3
}

private fun Int.toDefaultImportance(): Int = when (this.toBurdenValue()) {
    1 -> 2
    5 -> 4
    else -> 3
}

private fun Int.toDefaultFocusNeed(): Int = when (this.toBurdenValue()) {
    1 -> 2
    5 -> 4
    else -> 3
}
