package com.ownlifeos.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ownlifeos.domain.automation.EstimatedCheckIn
import com.ownlifeos.domain.model.BatteryFactor
import com.ownlifeos.domain.model.ForecastRiskLevel
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.RankedTask
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.RebalancedDayPlan
import com.ownlifeos.domain.model.RecoveryPlan
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.model.TodayMode
import com.ownlifeos.ui.components.CommandLineItem
import com.ownlifeos.ui.components.FluidButton
import com.ownlifeos.ui.components.FluidButtonStyle
import com.ownlifeos.ui.components.MetricCard
import com.ownlifeos.ui.components.MetricRing
import com.ownlifeos.ui.components.OwnLifeCard
import com.ownlifeos.ui.components.ReasonList
import com.ownlifeos.ui.components.SectionHeader
import com.ownlifeos.ui.components.SignalChipRow
import com.ownlifeos.ui.components.StatusPill
import com.ownlifeos.ui.motion.ownLifeEnterTransition
import com.ownlifeos.ui.motion.ownLifeExitTransition
import com.ownlifeos.ui.motion.rememberMotionEnabled
import com.ownlifeos.util.DateUtils

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenMorning: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenEvening: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenForecast: () -> Unit,
    onOpenSimulation: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenPattern: () -> Unit,
    onConfirmAutoCheckIn: () -> Unit,
    onRecordStrategyFeedback: (RecommendationFeedbackType) -> Unit
) {
    val motionEnabled = rememberMotionEnabled()
    var visible by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var showTodayMore by remember { mutableStateOf(false) }
    var showRecommendationMore by remember { mutableStateOf(false) }
    var showEvidenceMore by remember { mutableStateOf(false) }
    var showPatternMore by remember { mutableStateOf(false) }
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
            if (uiState.checkIn == null) {
                AutoStateEstimateCard(
                    analysis = uiState.analysis,
                    forecast = uiState.forecast,
                    rebalancedPlan = uiState.rebalancedPlan,
                    estimatedCheckIn = uiState.estimatedCheckIn,
                    onAccept = onConfirmAutoCheckIn,
                    onEdit = onOpenMorning
                )
            }

            if (uiState.checkIn != null) {
                uiState.analysis?.let { analysis ->
                    DailyOperatingStrategyCard(
                        analysis = analysis,
                        forecast = uiState.forecast,
                        rebalancedPlan = uiState.rebalancedPlan,
                        recoveryPlan = uiState.recoveryPlan,
                        primaryMemo = uiState.checkIn.memo,
                        latestFeedback = uiState.latestStrategyFeedback,
                        onOpenForecast = onOpenForecast,
                        onEditStatus = onOpenMorning,
                        onFeedback = onRecordStrategyFeedback
                    )
                }

                QuickActions(
                    onOpenTasks = onOpenTasks,
                    onOpenSimulation = onOpenSimulation
                )

                FluidButton(
                    text = if (showDetails) "자세히 접기" else "자세히 보기",
                    onClick = { showDetails = !showDetails },
                    modifier = Modifier.fillMaxWidth(),
                    style = FluidButtonStyle.Outlined,
                    expandContent = true
                )
                AnimatedVisibility(visible = showDetails) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionHeader(title = "오늘 상태", trailing = "핵심 지표")
                        MetricCard(
                            title = "생활 배터리",
                            value = uiState.metrics.lifeBattery,
                            supportingText = "Life Battery · 오늘 감당 가능한 처리량을 로컬 기록으로 계산",
                            icon = Icons.Outlined.Bolt,
                            color = MaterialTheme.colorScheme.primary,
                            motionEnabled = motionEnabled,
                            modifier = Modifier.fillMaxWidth()
                        )
                        MetricCard(
                            title = "집중 상태",
                            value = uiState.metrics.focusLevel,
                            supportingText = "Focus Level · 현재 컨디션과 집중 필요 작업을 함께 계산",
                            icon = Icons.Outlined.CenterFocusStrong,
                            color = MaterialTheme.colorScheme.secondary,
                            motionEnabled = motionEnabled,
                            modifier = Modifier.fillMaxWidth()
                        )
                        MetricCard(
                            title = "스트레스 부하",
                            value = uiState.metrics.stressLevel,
                            supportingText = "Stress Load · 부담감, 미완료 누적, 작업 에너지 기반",
                            icon = Icons.Outlined.WarningAmber,
                            color = MaterialTheme.colorScheme.error,
                            motionEnabled = motionEnabled,
                            modifier = Modifier.fillMaxWidth()
                        )
                        FluidButton(
                            text = if (showTodayMore) "오늘 상태 접기" else "오늘 상태 더 보기",
                            onClick = { showTodayMore = !showTodayMore },
                            modifier = Modifier.fillMaxWidth(),
                            style = FluidButtonStyle.Outlined,
                            expandContent = true
                        )
                        AnimatedVisibility(visible = showTodayMore) {
                            TodayHeader(
                                date = uiState.date,
                                hasCheckIn = true,
                                hasReview = uiState.review != null
                            )
                        }

                        SectionHeader(title = "오늘 추천", trailing = "운영 전략")
                        FluidButton(
                            text = if (showRecommendationMore) "추천 접기" else "추천 더 보기",
                            onClick = { showRecommendationMore = !showRecommendationMore },
                            modifier = Modifier.fillMaxWidth(),
                            style = FluidButtonStyle.Outlined,
                            expandContent = true
                        )
                        AnimatedVisibility(visible = showRecommendationMore) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                TaskKillCard(
                                    rebalancedPlan = uiState.rebalancedPlan,
                                    onOpenForecast = onOpenForecast
                                )

                                FailurePreventionCard(
                                    analysis = uiState.analysis,
                                    forecast = uiState.forecast,
                                    rebalancedPlan = uiState.rebalancedPlan
                                )

                                ForecastSummary(
                                    highestRiskLabel = uiState.forecast?.highestRisk?.let {
                                        "${it.timeBlock.label} · ${it.riskLevel.label} ${it.riskScore}"
                                    } ?: "예측 계산 중",
                                    summary = uiState.forecast?.highestRisk?.summary ?: "시간대별 부하 예측을 준비하고 있습니다.",
                                    onOpenForecast = onOpenForecast
                                )

                                RecoverySummary(
                                    title = uiState.recoveryPlan?.title ?: "회복 플랜",
                                    action = uiState.recoveryPlan?.actions?.firstOrNull()?.let {
                                        "${it.title} · ${it.durationMinutes}분"
                                    } ?: "실행 가능한 회복 액션을 계산하고 있습니다.",
                                    onOpenForecast = onOpenForecast
                                )

                                RankedTaskPreview(rankedTasks = uiState.metrics.rankedTasks.take(4))
                            }
                        }

                        SectionHeader(title = "근거", trailing = "로컬 계산")
                        FluidButton(
                            text = if (showEvidenceMore) "근거 접기" else "근거 보기",
                            onClick = { showEvidenceMore = !showEvidenceMore },
                            modifier = Modifier.fillMaxWidth(),
                            style = FluidButtonStyle.Outlined,
                            expandContent = true
                        )
                        AnimatedVisibility(visible = showEvidenceMore) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                uiState.analysis?.let { analysis ->
                                    LifeBatteryEvidenceCard(factors = analysis.batteryFactors)
                                }
                                uiState.analysis?.let { analysis ->
                                    ErrorSignalList(analysis = analysis)
                                }
                            }
                        }

                        SectionHeader(title = "패턴", trailing = "장기 분석")
                        FluidButton(
                            text = if (showPatternMore) "패턴 접기" else "패턴 보기",
                            onClick = { showPatternMore = !showPatternMore },
                            modifier = Modifier.fillMaxWidth(),
                            style = FluidButtonStyle.Outlined,
                            expandContent = true
                        )
                        AnimatedVisibility(visible = showPatternMore) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                SystemHealthSummary(
                                    score = uiState.systemHealthReport?.healthScore,
                                    strategy = uiState.systemHealthReport?.nextWeekStrategies?.firstOrNull() ?: "최근 7일 시스템 건강도를 계산하고 있습니다.",
                                    onOpenHealth = onOpenHealth
                                )

                                AdvancedNavigationCard(
                                    onOpenMorning = onOpenMorning,
                                    onOpenEvening = onOpenEvening,
                                    onOpenReport = onOpenReport,
                                    onOpenHealth = onOpenHealth,
                                    onOpenPattern = onOpenPattern
                                )

                                LocalPrivacyNote()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayHeader(
    date: String,
    hasCheckIn: Boolean,
    hasReview: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = DateUtils.displayDate(date),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = buildString {
                    append(if (hasCheckIn) "아침 체크인 완료" else "아침 체크인 대기")
                    append(" · ")
                    append(if (hasReview) "저녁 회고 저장됨" else "저녁 회고 대기")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SystemBootCard(onGenerateStrategy: () -> Unit) {
    OwnLifeCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        StatusPill(
            text = "System Booting",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "오늘의 나를 부팅합니다.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "컨디션, 부담, 가장 중요한 일 하나만 입력하면 감당 가능한 운영 전략을 바로 만듭니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FluidButton(
            text = "오늘 전략 만들기",
            onClick = onGenerateStrategy,
            modifier = Modifier.fillMaxWidth(),
            expandContent = true
        )
    }
}

@Composable
private fun AutoStateEstimateCard(
    analysis: TodaySystemAnalysis?,
    forecast: FutureLoadForecast?,
    rebalancedPlan: RebalancedDayPlan?,
    estimatedCheckIn: EstimatedCheckIn?,
    onAccept: () -> Unit,
    onEdit: () -> Unit
) {
    if (analysis == null) {
        SystemBootCard(onGenerateStrategy = onEdit)
        return
    }

    val status = statusPresentation(analysis, forecast, rebalancedPlan)
    val firstAction = rebalancedPlan?.nowTasks?.firstOrNull()?.task?.title
        ?: estimatedCheckIn?.memo?.takeIf { it.isNotBlank() }
        ?: "가벼운 작업 1개부터 시작"
    val shortTask = todayShortTask(rebalancedPlan, analysis, estimatedCheckIn?.memo)
    val reasonLabels = compactReasonLabels(analysis, forecast)

    OwnLifeCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "오늘 상태를 자동으로 정리했습니다.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "System Booting...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusPill(
                    text = status.label,
                    containerColor = status.containerColor,
                    contentColor = status.contentColor
                )
            }
            MetricRing(
                value = analysis.lifeBattery,
                label = "Battery",
                color = status.accentColor,
                size = 82.dp
            )
        }
        Text(
            text = operationGuide(status),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        CommandLineItem(
            label = "지금 할 일",
            value = firstAction,
            accentColor = status.accentColor,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        CommandLineItem(
            label = "짧게만 할 일",
            value = shortTask,
            accentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
        CompactReasonLabels(
            labels = reasonLabels,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        if (estimatedCheckIn?.reasons?.isNotEmpty() == true) {
            Text(
                text = estimatedCheckIn.reasons.take(2).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FluidButton(
                text = "그대로 시작",
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                expandContent = true
            )
            FluidButton(
                text = "수정하기",
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                style = FluidButtonStyle.Outlined,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                expandContent = true
            )
        }
    }
}

@Composable
private fun PrimaryStrategyButton(
    hasCheckIn: Boolean,
    onGenerateStrategy: () -> Unit
) {
    FluidButton(
        text = if (hasCheckIn) "오늘 전략 보기" else "오늘 전략 만들기",
        onClick = onGenerateStrategy,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        expandContent = true
    )
}

@Composable
private fun DailyOperatingStrategyCard(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?,
    rebalancedPlan: RebalancedDayPlan?,
    recoveryPlan: RecoveryPlan?,
    primaryMemo: String?,
    latestFeedback: RecommendationFeedback?,
    onOpenForecast: () -> Unit,
    onEditStatus: () -> Unit,
    onFeedback: (RecommendationFeedbackType) -> Unit
) {
    var showWhy by remember { mutableStateOf(false) }
    val highestRisk = forecast?.highestRisk
    val status = statusPresentation(analysis, forecast, rebalancedPlan)
    val firstAction = rebalancedPlan?.nowTasks?.firstOrNull()?.task?.title
        ?: recoveryPlan?.actions?.firstOrNull()?.title
        ?: "30초 체크인으로 오늘 상태 확정"
    val shortTask = todayShortTask(rebalancedPlan, analysis, primaryMemo)
    val shortTaskTitle = shortTask.removeSuffix(" 25분")
    val avoid = todayAvoidance(analysis, forecast, shortTaskTitle)
    val reasonLabels = compactReasonLabels(analysis, forecast)

    OwnLifeCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.TipsAndUpdates,
                        contentDescription = null,
                        tint = status.accentColor
                    )
                    Text(
                        text = "오늘의 운영 전략",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusPill(
                    text = status.label,
                    containerColor = status.containerColor,
                    contentColor = status.contentColor
                )
                Text(
                    text = operationGuide(status),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            MetricRing(
                value = analysis.lifeBattery,
                label = "Battery",
                color = status.accentColor,
                size = 84.dp,
                motionEnabled = true
            )
        }
        SignalChipRow(
            items = listOf(
                "생활 배터리" to "${analysis.lifeBattery}%",
                "스트레스 부하" to "${analysis.stressLoad}%",
                "집중 상태" to "${analysis.focusLevel}%"
            )
        )
        CommandLineItem(
            label = "지금 할 일",
            value = firstAction,
            accentColor = status.accentColor,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CommandLineItem(
            label = "짧게만 할 일",
            value = shortTask,
            accentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CommandLineItem(
            label = "오늘 피할 것",
            value = avoid,
            accentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        CompactReasonLabels(
            labels = reasonLabels,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FluidButton(
                text = "오늘 전략 보기",
                onClick = onOpenForecast,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                expandContent = true
            )
            FluidButton(
                text = "수정하기",
                onClick = onEditStatus,
                modifier = Modifier.weight(1f),
                style = FluidButtonStyle.Outlined,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                expandContent = true
            )
        }
        FluidButton(
            text = if (showWhy) "이유 접기" else "이유 보기",
            onClick = { showWhy = !showWhy },
            modifier = Modifier.fillMaxWidth(),
            style = FluidButtonStyle.Outlined,
            expandContent = true
        )
        AnimatedVisibility(visible = showWhy) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = todayJudgement(analysis, forecast),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "점수: Battery ${analysis.lifeBattery} · Stress ${analysis.stressLoad} · Focus ${analysis.focusLevel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                highestRisk?.let {
                    Text(
                        text = "부하 예측: ${it.timeBlock.label} ${it.riskLevel.userFacingLabel()} ${it.riskScore}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ReasonList(reasons = analysis.modeReasons, limit = 4)
                StateCorrectionRow(onFeedback = onFeedback)
                RecommendationFeedbackRow(
                    latestFeedback = latestFeedback,
                    onFeedback = onFeedback
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompactReasonLabels(
    labels: List<String>,
    contentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "추천 근거",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            labels.forEach { label ->
                StatusPill(
                    text = label,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    horizontalPadding = 9.dp,
                    verticalPadding = 5.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StateCorrectionRow(
    onFeedback: (RecommendationFeedbackType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "오늘 상태를 다르게 느끼시나요?",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FluidButton(
                text = "더 괜찮음",
                onClick = { onFeedback(RecommendationFeedbackType.TOO_CONSERVATIVE) },
                style = FluidButtonStyle.Outlined,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            )
            FluidButton(
                text = "더 힘듦",
                onClick = { onFeedback(RecommendationFeedbackType.TOO_AGGRESSIVE) },
                style = FluidButtonStyle.Outlined,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            )
            FluidButton(
                text = "잘 모르겠음",
                onClick = { onFeedback(RecommendationFeedbackType.NOT_CONTEXTUAL) },
                style = FluidButtonStyle.Outlined,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecommendationFeedbackRow(
    latestFeedback: RecommendationFeedback?,
    onFeedback: (RecommendationFeedbackType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "이 추천이 맞았나요?",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RecommendationFeedbackType.entries.forEach { type ->
                FluidButton(
                    text = type.label,
                    onClick = { onFeedback(type) },
                    style = FluidButtonStyle.Outlined,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
        latestFeedback?.let {
            Text(
                text = "최근 피드백: ${it.feedbackType.label}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun QuickActions(
    onOpenTasks: () -> Unit,
    onOpenSimulation: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "빠른 실행")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FluidButton(
                text = "작업 큐 정리",
                onClick = onOpenTasks,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                expandContent = true
            )
            FluidButton(
                text = "선택 시뮬레이션",
                onClick = onOpenSimulation,
                modifier = Modifier.weight(1f),
                style = FluidButtonStyle.Tonal,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                expandContent = true
            )
        }
    }
}

@Composable
private fun LocalPrivacyNote() {
    Text(
        text = "나의 하루 기록은 이 기기 안에서만 처리됩니다.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun AdvancedNavigationCard(
    onOpenMorning: () -> Unit,
    onOpenEvening: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenPattern: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "추가 화면")
            Text(
                text = "기록과 장기 분석은 필요할 때만 열어봅니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FluidButton(
                    text = "체크인",
                    onClick = onOpenMorning,
                    modifier = Modifier.weight(1f),
                    style = FluidButtonStyle.Tonal,
                    expandContent = true
                )
                FluidButton(
                    text = "회고",
                    onClick = onOpenEvening,
                    modifier = Modifier.weight(1f),
                    style = FluidButtonStyle.Tonal,
                    expandContent = true
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FluidButton(
                    text = "주간 요약",
                    onClick = onOpenReport,
                    modifier = Modifier.weight(1f),
                    style = FluidButtonStyle.Tonal,
                    expandContent = true
                )
                FluidButton(
                    text = "건강도",
                    onClick = onOpenHealth,
                    modifier = Modifier.weight(1f),
                    style = FluidButtonStyle.Tonal,
                    expandContent = true
                )
                FluidButton(
                    text = "내 패턴",
                    onClick = onOpenPattern,
                    modifier = Modifier.weight(1f),
                    style = FluidButtonStyle.Tonal,
                    expandContent = true
                )
            }
        }
    }
}

@Composable
private fun ForecastSummary(
    highestRiskLabel: String,
    summary: String,
    onOpenForecast: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "부하 예측", trailing = "Future Load Forecast")
            Text(
                text = highestRiskLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FluidButton(
                text = "시간대별 예측 보기",
                onClick = onOpenForecast,
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Tonal,
                expandContent = true
            )
        }
    }
}

@Composable
private fun TaskKillCard(
    rebalancedPlan: RebalancedDayPlan?,
    onOpenForecast: () -> Unit
) {
    var showWhy by remember { mutableStateOf(false) }
    val killCandidates = rebalancedPlan?.deferCandidates.orEmpty()
    val alternatives = rebalancedPlan?.quickWinTasks.orEmpty()

    OwnLifeCard(modifier = Modifier.fillMaxWidth()) {
        StatusPill(
            text = "Task Kill",
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title = "오늘 줄일 일", trailing = "Task Kill")
            if (killCandidates.isEmpty()) {
                Text(
                    text = "지금은 넘겨도 되는 작업 신호가 강하지 않습니다. 큐를 그대로 유지해도 부담이 크지 않아 보입니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "오늘 상태에서는 아래 작업이 부담될 수 있습니다. 가능하면 내일로 넘기거나 범위를 줄여보세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                killCandidates.take(3).forEachIndexed { index, task ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${index + 1}. ${task.task.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = task.reasons.firstOrNull()?.detail ?: "현재 상태와 작업 비용을 기준으로 보류 후보로 분류했습니다.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (alternatives.isNotEmpty()) {
                    Text(
                        text = "대신 추천",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    alternatives.take(2).forEach {
                        Text(
                            text = "- ${it.task.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            FluidButton(
                text = if (showWhy) "근거 접기" else "왜 줄이는 추천인가요?",
                onClick = { showWhy = !showWhy },
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Outlined,
                expandContent = true
            )
            AnimatedVisibility(visible = showWhy) {
                val reasons = killCandidates.firstOrNull()?.reasons ?: rebalancedPlan?.reasons.orEmpty()
                if (reasons.isEmpty()) {
                    Text(
                        text = "남은 작업 수, 에너지 소모량, 마감일, 현재 배터리를 함께 계산합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ReasonList(reasons = reasons, limit = 3)
                }
            }
            FluidButton(
                text = "작업 큐 정리하기",
                onClick = onOpenForecast,
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Tonal,
                expandContent = true
            )
        }
    }
}

@Composable
private fun FailurePreventionCard(
    analysis: TodaySystemAnalysis?,
    forecast: FutureLoadForecast?,
    rebalancedPlan: RebalancedDayPlan?
) {
    val highestRisk = forecast?.highestRisk
    val riskText = when {
        highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK -> "${highestRisk.timeBlock.label} 과부하 위험이 높습니다."
        analysis?.stressLoad ?: 0 >= 70 -> "현재 부담이 높아 새 작업 추가가 실패 위험을 키울 수 있습니다."
        analysis?.lifeBattery ?: 100 <= 45 -> "배터리가 낮아 고난도 작업부터 시작하면 흔들릴 수 있습니다."
        else -> "큰 실패 위험은 낮지만 작업 범위를 작게 유지하는 것이 좋습니다."
    }
    val easyTask = rebalancedPlan?.quickWinTasks?.firstOrNull()?.task?.title

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "오늘의 실패 방지", trailing = "Failure Guard")
            Text(
                text = riskText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            val actions = listOfNotNull(
                easyTask?.let { "쉬운 작업 '$it' 먼저 완료" },
                "어려운 작업은 1개만 남기기",
                "밤 11시 이후 새 작업 시작 금지"
            )
            actions.forEach { action ->
                Text(
                    text = "- $action",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LifeBatteryEvidenceCard(factors: List<BatteryFactor>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = "생활 배터리 근거", trailing = "Life Battery")
            if (factors.isEmpty()) {
                Text(
                    text = "오늘 기록이 적어 기본값 중심으로 계산했습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val reductions = factors.filterNot { it.isRecovery }.take(4)
                val recoveries = factors.filter { it.isRecovery }.take(3)
                if (reductions.isNotEmpty()) {
                    Text(
                        text = "감소 요인",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    reductions.forEach { factor ->
                        BatteryFactorRow(factor)
                    }
                }
                if (recoveries.isNotEmpty()) {
                    Text(
                        text = "회복 요인",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    recoveries.forEach { factor ->
                        BatteryFactorRow(factor)
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryFactorRow(factor: BatteryFactor) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "${factor.title}: ${if (factor.points > 0) "+" else ""}${factor.points}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = factor.detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PendingProcessCard(
    rebalancedPlan: RebalancedDayPlan?,
    onOpenForecast: () -> Unit
) {
    val deferCandidates = rebalancedPlan?.deferCandidates.orEmpty()
    val alternatives = rebalancedPlan?.quickWinTasks.orEmpty()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Pending Process", trailing = "하지 않을 일 정하기")
            if (deferCandidates.isEmpty()) {
                Text(
                    text = "오늘 보류할 만한 작업 신호가 강하지 않습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "오늘은 아래 작업을 뒤로 미루거나 범위를 줄이는 것이 좋습니다.",
                    style = MaterialTheme.typography.bodyMedium
                )
                deferCandidates.take(3).forEach {
                    Text(
                        text = "- ${it.task.title} · ${it.action.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (alternatives.isNotEmpty()) {
                    Text(
                        text = "대신",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    alternatives.take(2).forEach {
                        Text(
                            text = "- ${it.task.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            FluidButton(
                text = "작업 순서 다시 짜기",
                onClick = onOpenForecast,
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Tonal,
                expandContent = true
            )
        }
    }
}

@Composable
private fun RecoverySummary(
    title: String,
    action: String,
    onOpenForecast: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "회복 플랜", trailing = "Recovery Process")
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(
                text = action,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FluidButton(
                text = "회복 플랜 보기",
                onClick = onOpenForecast,
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Tonal,
                expandContent = true
            )
        }
    }
}

@Composable
private fun SystemHealthSummary(
    score: Int?,
    strategy: String,
    onOpenHealth: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(
                title = "생활 시스템 건강도",
                trailing = score?.let { "$it / 100" } ?: "계산 중"
            )
            Text(
                text = strategy,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FluidButton(
                text = "시스템 건강도 보기",
                onClick = onOpenHealth,
                modifier = Modifier.fillMaxWidth(),
                style = FluidButtonStyle.Tonal,
                expandContent = true
            )
        }
    }
}

@Composable
private fun RankedTaskPreview(rankedTasks: List<RankedTask>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "추천 작업 순서")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (rankedTasks.isEmpty()) {
                    Text(
                        text = "아직 작업 큐 데이터가 부족합니다. 오늘 감당 가능한 일 1개만 넣으면 추천 순서를 만들 수 있습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    rankedTasks.forEachIndexed { index, ranked ->
                        Text(
                            text = "${index + 1}. ${ranked.task.title} · 추천 ${ranked.score}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        ranked.reasons.firstOrNull()?.let {
                            Text(
                                text = it.detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorSignalList(analysis: TodaySystemAnalysis) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "오늘의 문제", trailing = "Error Log")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (analysis.errorSignals.isEmpty()) {
                    Text(
                        text = "강한 반복 문제는 보이지 않습니다. 3일 정도 사용하면 미완료 누적이나 피로 패턴을 더 잘 잡아냅니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    analysis.errorSignals.forEach { signal ->
                        Text(
                            text = signal.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = signal.detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun systemStatusLabel(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?
): String {
    val highRisk = forecast?.highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK
    return when {
        highRisk -> "System Status: 과부하 위험"
        analysis.mode == TodayMode.EMERGENCY -> "System Status: 긴급 정리 필요"
        analysis.mode == TodayMode.RECOVERY -> "System Status: 회복 필요"
        analysis.mode == TodayMode.LOW_POWER || analysis.mode == TodayMode.MAINTENANCE -> {
            "System Status: 저전력 모드"
        }
        else -> "System Status: 정상 작동"
    }
}

private data class StatusPresentation(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val accentColor: Color
)

@Composable
private fun statusPresentation(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?,
    rebalancedPlan: RebalancedDayPlan? = null
): StatusPresentation {
    val darkTheme = isSystemInDarkTheme()
    val highRisk = forecast?.highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK
    val hasDeferCandidate = rebalancedPlan?.deferCandidates?.isNotEmpty() == true
    val hasWarningFactor = analysis.batteryFactors.any {
        it.title.contains("수면") ||
            it.title.contains("작업") ||
            it.title.contains("미완료") ||
            it.title.contains("피로") ||
            it.title.contains("부담")
    }
    return when {
        analysis.mode == TodayMode.EMERGENCY || analysis.lifeBattery <= 35 -> StatusPresentation(
            label = "회복 필요",
            containerColor = if (darkTheme) Color(0xFF4B1F29) else Color(0xFFFFE8EC),
            contentColor = if (darkTheme) Color(0xFFFFD9E0) else Color(0xFF7A2530),
            accentColor = MaterialTheme.colorScheme.error
        )
        highRisk || analysis.stressLoad >= 70 -> StatusPresentation(
            label = "과부하 주의",
            containerColor = if (darkTheme) Color(0xFF4A3215) else Color(0xFFFFF0E1),
            contentColor = if (darkTheme) Color(0xFFFFDDB4) else Color(0xFF6A3905),
            accentColor = if (darkTheme) Color(0xFFFFC66D) else Color(0xFFD47D00)
        )
        analysis.mode == TodayMode.RECOVERY || analysis.lifeBattery <= 45 -> StatusPresentation(
            label = "회복 필요",
            containerColor = if (darkTheme) Color(0xFF252C55) else Color(0xFFEEF3FF),
            contentColor = if (darkTheme) Color(0xFFE5E8FF) else Color(0xFF343469),
            accentColor = MaterialTheme.colorScheme.primary
        )
        analysis.mode == TodayMode.LOW_POWER ||
            analysis.mode == TodayMode.MAINTENANCE ||
            hasDeferCandidate ||
            hasWarningFactor ||
            analysis.lifeBattery <= 59 ||
            analysis.stressLoad >= 55 -> StatusPresentation(
            label = "주의",
            containerColor = if (darkTheme) Color(0xFF40350F) else Color(0xFFFFF6D8),
            contentColor = if (darkTheme) Color(0xFFFFE08A) else Color(0xFF584000),
            accentColor = if (darkTheme) Color(0xFFEBC86D) else Color(0xFFD89A1C)
        )
        analysis.lifeBattery in 60..79 -> StatusPresentation(
            label = "보통",
            containerColor = if (darkTheme) Color(0xFF163247) else Color(0xFFEAF6FF),
            contentColor = if (darkTheme) Color(0xFFD3ECFF) else Color(0xFF163C5A),
            accentColor = MaterialTheme.colorScheme.primary
        )
        else -> StatusPresentation(
            label = "안정",
            containerColor = if (darkTheme) Color(0xFF113B35) else Color(0xFFEAF8F4),
            contentColor = if (darkTheme) Color(0xFFBDEFE5) else Color(0xFF0C4B40),
            accentColor = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun operationGuide(status: StatusPresentation): String = when (status.label) {
    "안정" -> "집중 운영 · 어려운 일 먼저"
    "보통" -> "평소 운영 · 계획대로 진행"
    "주의" -> "저전력 운영 · 쉬운 일부터 시작"
    "과부하 주의" -> "저전력 운영 · 작업 범위 줄이기"
    "회복 필요" -> "회복 운영 · 새 작업 금지"
    else -> "저전력 운영 · 쉬운 일부터 시작"
}

private fun TodayMode.userFacingLabel(): String = when (this) {
    TodayMode.PERFORMANCE,
    TodayMode.FOCUS -> "집중 모드"

    TodayMode.BALANCED,
    TodayMode.MAINTENANCE -> "보통 모드"

    TodayMode.LOW_POWER -> "저전력 모드"

    TodayMode.RECOVERY,
    TodayMode.EMERGENCY -> "회복 모드"
}

private fun TodayMode.actionGuide(): String = when (this) {
    TodayMode.PERFORMANCE,
    TodayMode.FOCUS -> "어려운 일 먼저 처리"

    TodayMode.BALANCED,
    TodayMode.MAINTENANCE -> "평소처럼 진행"

    TodayMode.LOW_POWER -> "쉬운 일부터 처리"

    TodayMode.RECOVERY,
    TodayMode.EMERGENCY -> "새 작업 추가 보류"
}

private fun ForecastRiskLevel.userFacingLabel(): String = when (this) {
    ForecastRiskLevel.STABLE -> "안정"
    ForecastRiskLevel.CAUTION -> "주의"
    ForecastRiskLevel.HIGH_RISK -> "높음"
}

private fun todayDeferTask(
    rebalancedPlan: RebalancedDayPlan?,
    analysis: TodaySystemAnalysis
): String {
    return rebalancedPlan?.deferCandidates?.firstOrNull()?.task?.title ?: when {
        analysis.stressLoad >= 70 -> "새 작업 추가"
        analysis.lifeBattery <= 45 -> "부담 큰 작업"
        analysis.metricsNeedQueueCut() -> "마감 없는 큰 작업"
        else -> "계획에 없는 큰 작업"
    }
}

private fun todayShortTask(
    rebalancedPlan: RebalancedDayPlan?,
    analysis: TodaySystemAnalysis,
    primaryMemo: String?
): String {
    val memo = primaryMemo?.trim().orEmpty()
    val candidate = listOf(
        rebalancedPlan?.deferCandidates.orEmpty(),
        rebalancedPlan?.laterTasks.orEmpty(),
        rebalancedPlan?.nowTasks.orEmpty()
    ).flatten()
        .firstOrNull { task ->
            memo.isNotBlank() && (
                task.task.title.contains(memo, ignoreCase = true) ||
                    memo.contains(task.task.title, ignoreCase = true)
                )
        }
        ?: rebalancedPlan?.deferCandidates?.firstOrNull()
        ?: rebalancedPlan?.laterTasks?.firstOrNull { it.task.energyCost >= 4 || it.task.focusNeed >= 4 }
        ?: rebalancedPlan?.nowTasks?.firstOrNull { it.task.energyCost >= 4 || it.task.focusNeed >= 4 }

    return candidate?.task?.title?.let { "$it 25분" } ?: when {
        memo.isNotBlank() -> "$memo 25분"
        analysis.lifeBattery <= 59 || analysis.stressLoad >= 55 -> "큰 작업 25분"
        else -> "중요한 작업 25분"
    }
}

private fun TodaySystemAnalysis.metricsNeedQueueCut(): Boolean =
    stressLoad >= 60 || fatigueLoad >= 60 || lifeBattery <= 55

private fun compactReasonLabels(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?
): List<String> {
    val labels = linkedSetOf<String>()
    val factorTitles = analysis.batteryFactors.map { it.title }

    if (factorTitles.any { it.contains("수면") }) labels.add("수면 부족")
    if (factorTitles.any { it.contains("작업 에너지") || it.contains("남은 작업") }) labels.add("할 일 많음")
    if (factorTitles.any { it.contains("미완료") }) labels.add("어제 미완료")
    if (factorTitles.any { it.contains("피로") }) labels.add("피로 누적")
    if (analysis.stressLoad >= 70) labels.add("부담 높음")
    if (forecast?.highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK) labels.add("과부하 위험")
    if (analysis.focusLevel < 45) labels.add("집중 낮음")
    if (labels.isEmpty()) labels.add("기록 기반")
    if (labels.size == 1) labels.add("작업 큐 반영")
    if (labels.size == 2) labels.add("현재 상태 반영")

    return labels.take(3)
}

private fun primaryReasonSummary(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?
): String {
    val forecastReason = forecast?.highestRisk?.summary?.takeIf { it.isNotBlank() }
    val modeReason = analysis.modeReasons.firstOrNull()?.title
    val fallback = when {
        analysis.lifeBattery <= 45 -> "배터리가 낮아 작업 범위를 작게 잡았습니다."
        analysis.stressLoad >= 70 -> "부담 신호가 높아 큐를 줄이는 쪽으로 판단했습니다."
        analysis.focusLevel >= 70 -> "집중 여력이 있어 중요한 작업 1개를 먼저 배치했습니다."
        else -> "오늘 기록과 남은 작업량을 함께 계산했습니다."
    }
    return forecastReason ?: modeReason ?: fallback
}

private fun todayJudgement(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?
): String {
    val highestRisk = forecast?.highestRisk
    return when {
        highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK -> {
            "${highestRisk.timeBlock.label}에 무리할 가능성이 높습니다. 작업 범위를 줄이고 쉬운 작업부터 시작하는 편이 좋습니다."
        }
        analysis.stressLoad >= 70 -> "부담이 높은 날입니다. 새 작업을 늘리기보다 남은 큐를 정리하는 쪽이 좋습니다."
        analysis.lifeBattery <= 45 -> "배터리가 낮습니다. 큰 작업보다 짧고 끝이 보이는 작업으로 운영하는 편이 좋습니다."
        analysis.focusLevel >= 70 -> "집중 여력이 있습니다. 오전이나 초반에 중요한 작업 1개를 먼저 처리하는 흐름이 좋습니다."
        else -> analysis.mode.description
    }
}

private fun todayAvoidance(
    analysis: TodaySystemAnalysis,
    forecast: FutureLoadForecast?,
    shortTaskTitle: String?
): String {
    val highRisk = forecast?.highestRisk?.riskLevel == ForecastRiskLevel.HIGH_RISK
    val taskTitle = shortTaskTitle
        ?.takeIf { it.isNotBlank() && it != "큰 작업" && it != "중요한 작업" }
    return when {
        highRisk -> "고에너지 작업 추가"
        taskTitle != null && (analysis.lifeBattery <= 59 || analysis.stressLoad >= 55) -> "${taskTitle}을 길게 잡기"
        analysis.stressLoad >= 70 -> "범위가 큰 새 작업 시작"
        analysis.lifeBattery <= 45 -> "밤 11시 이후 새 작업 시작"
        analysis.focusLevel < 45 -> "집중이 오래 필요한 작업부터 시작"
        else -> "계획에 없는 큰 작업 끼워 넣기"
    }
}
