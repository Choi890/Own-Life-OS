package com.ownlifeos.ui.screens.evening

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Sync
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
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import com.ownlifeos.util.DateUtils

@Composable
fun EveningReviewScreen(
    uiState: EveningReviewUiState,
    pendingTasks: List<DailyTask>,
    onGoodThingsChange: (String) -> Unit,
    onErrorLogsChange: (String) -> Unit,
    onCarryOverChange: (String) -> Unit,
    onUsePendingTasks: () -> Unit,
    onApplyDraft: () -> Unit,
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "저녁 회고",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateUtils.displayDate(uiState.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Evening Review",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionHeader(title = "회고 초안", trailing = "Auto Draft")
                    Text(
                        text = uiState.draft.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "오늘 잘 된 점: ${uiState.draft.goodThings.lineSequence().firstOrNull().orEmpty()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "오늘의 문제: ${uiState.draft.errorLogs.lineSequence().firstOrNull().orEmpty()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FluidButton(
                        text = "초안 적용",
                        onClick = onApplyDraft,
                        modifier = Modifier.fillMaxWidth(),
                        style = FluidButtonStyle.Tonal,
                        icon = Icons.Outlined.Sync,
                        expandContent = true
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.goodThings,
                        onValueChange = onGoodThingsChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("오늘 잘 된 점") },
                        minLines = 3,
                        maxLines = 7
                    )

                    OutlinedTextField(
                        value = uiState.errorLogs,
                        onValueChange = onErrorLogsChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("오늘의 오류 로그") },
                        minLines = 3,
                        maxLines = 7
                    )

                    OutlinedTextField(
                        value = uiState.carryOver,
                        onValueChange = onCarryOverChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("내일로 넘길 것") },
                        minLines = 3,
                        maxLines = 7
                    )

                    FluidButton(
                        text = "미완료 작업 불러오기",
                        onClick = onUsePendingTasks,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = pendingTasks.any { it.status != TaskStatus.DONE },
                        style = FluidButtonStyle.Tonal,
                        icon = Icons.Outlined.Sync,
                        expandContent = true
                    )

                    FluidButton(
                        text = "회고 저장",
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.Save,
                        expandContent = true
                    )
                }
            }

            AnimatedVisibility(visible = uiState.saved) {
                Text(
                    text = "저녁 회고가 로컬 DB에 저장되었습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
