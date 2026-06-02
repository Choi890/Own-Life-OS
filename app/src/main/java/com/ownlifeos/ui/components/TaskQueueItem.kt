package com.ownlifeos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.RankedTask
import com.ownlifeos.domain.model.TaskStatus

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskQueueItem(
    task: DailyTask,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    rankedTask: RankedTask? = null,
    onDefer: () -> Unit = {}
) {
    var showCalculation by remember { mutableStateOf(false) }
    val action = taskActionLabel(task, rankedTask)
    val burden = taskBurdenLabel(task)

    OwnLifeCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(58.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(action.accentColor())
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatusPill(
                        text = burden,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        horizontalPadding = 9.dp,
                        verticalPadding = 5.dp
                    )
                    StatusPill(
                        text = task.status.label,
                        containerColor = action.containerColor(),
                        contentColor = action.contentColor(),
                        horizontalPadding = 9.dp,
                        verticalPadding = 5.dp
                    )
                    task.deadlineDate?.let {
                        StatusPill(
                            text = it,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            horizontalPadding = 9.dp,
                            verticalPadding = 5.dp
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = action.containerColor(),
            contentColor = action.contentColor()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "오늘 추천",
                    style = MaterialTheme.typography.labelSmall,
                    color = action.contentColor().copy(alpha = 0.72f)
                )
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = action.reason,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (task.status != TaskStatus.DONE) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FluidButton(
                    text = action.primaryButtonLabel(task.status),
                    onClick = { onStatusChange(TaskStatus.IN_PROGRESS) },
                    tone = FluidButtonTone.Start,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
                FluidButton(
                    text = "완료",
                    onClick = { onStatusChange(TaskStatus.DONE) },
                    style = FluidButtonStyle.Outlined,
                    tone = FluidButtonTone.Confirm,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
                FluidButton(
                    text = "내일로 미루기",
                    onClick = onDefer,
                    style = FluidButtonStyle.Outlined,
                    tone = FluidButtonTone.Delay,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        rankedTask?.let {
            FluidButton(
                text = if (showCalculation) "계산 기준 접기" else "계산 기준 보기",
                onClick = { showCalculation = !showCalculation },
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Outlined,
                expandContent = true
            )
            AnimatedVisibility(visible = showCalculation) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "오늘 적합도: ${it.score.toFitLabel()}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "세부 점수 ${it.score}점 · 중요도 ${task.importance} · 에너지 ${task.energyCost} · 집중 ${task.focusNeed}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ReasonList(
                        reasons = it.reasons,
                        limit = 2
                    )
                }
            }
        }
    }
}

private fun TaskAction.primaryButtonLabel(status: TaskStatus): String = when {
    status == TaskStatus.IN_PROGRESS && tone != TaskActionTone.Good -> "25분 타이머 시작"
    status == TaskStatus.IN_PROGRESS -> "진행 중"
    tone == TaskActionTone.Hold -> "25분만 시작"
    tone == TaskActionTone.Caution -> "25분만 시작"
    tone == TaskActionTone.Good -> "바로 시작"
    else -> "처리 완료"
}

private fun Int.toFitLabel(): String = when {
    this >= 70 -> "높음"
    this >= 40 -> "중간"
    else -> "낮음"
}

private data class TaskAction(
    val title: String,
    val reason: String,
    val tone: TaskActionTone
)

private enum class TaskActionTone {
    Good,
    Caution,
    Hold,
    Done
}

@Composable
private fun TaskAction.accentColor() = when (tone) {
    TaskActionTone.Good -> MaterialTheme.colorScheme.secondary
    TaskActionTone.Caution -> MaterialTheme.colorScheme.primary
    TaskActionTone.Hold -> MaterialTheme.colorScheme.error
    TaskActionTone.Done -> MaterialTheme.colorScheme.outline
}

@Composable
private fun TaskAction.containerColor() = when (tone) {
    TaskActionTone.Good -> MaterialTheme.colorScheme.secondaryContainer
    TaskActionTone.Caution -> MaterialTheme.colorScheme.primaryContainer
    TaskActionTone.Hold -> MaterialTheme.colorScheme.tertiaryContainer
    TaskActionTone.Done -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun TaskAction.contentColor() = when (tone) {
    TaskActionTone.Good -> MaterialTheme.colorScheme.onSecondaryContainer
    TaskActionTone.Caution -> MaterialTheme.colorScheme.onPrimaryContainer
    TaskActionTone.Hold -> MaterialTheme.colorScheme.onTertiaryContainer
    TaskActionTone.Done -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun taskActionLabel(
    task: DailyTask,
    rankedTask: RankedTask?
): TaskAction {
    val score = rankedTask?.score ?: 50
    val heavy = task.energyCost >= 4 || task.focusNeed >= 4
    val deferred = task.deferredCount > 0
    return when {
        task.status == TaskStatus.DONE -> TaskAction(
            title = "처리 완료",
            reason = "오늘 작업 큐 부담에서 제외했습니다.",
            tone = TaskActionTone.Done
        )
        heavy && score < 35 -> TaskAction(
            title = "오늘은 넘겨도 되는 일",
            reason = "부담이 큰 작업이라 현재 상태에서는 뒤로 미루는 편이 자연스럽습니다.",
            tone = TaskActionTone.Hold
        )
        heavy -> TaskAction(
            title = "짧게만 진행하세요",
            reason = "중요하지만 부담이 큰 작업입니다. 길게 잡기보다 25분만 추천합니다.",
            tone = TaskActionTone.Caution
        )
        score >= 55 -> TaskAction(
            title = "지금 처리하기 좋습니다",
            reason = "현재 상태에서 감당 가능성이 높은 작업입니다.",
            tone = TaskActionTone.Good
        )
        deferred -> TaskAction(
            title = "작게 나눠서 처리하세요",
            reason = "이미 미룬 기록이 있어 작은 단위로 줄이는 편이 좋습니다.",
            tone = TaskActionTone.Caution
        )
        else -> TaskAction(
            title = "가볍게 시작해도 됩니다",
            reason = "큰 부담 신호는 낮지만, 작업 범위를 작게 유지하는 편이 좋습니다.",
            tone = TaskActionTone.Good
        )
    }
}

private fun taskBurdenLabel(task: DailyTask): String = when {
    task.energyCost >= 4 || task.focusNeed >= 4 -> "부담 큼"
    task.energyCost <= 2 && task.focusNeed <= 2 -> "부담 낮음"
    else -> "부담 보통"
}
