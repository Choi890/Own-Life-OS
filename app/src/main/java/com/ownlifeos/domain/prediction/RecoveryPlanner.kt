package com.ownlifeos.domain.prediction

import com.ownlifeos.domain.model.ForecastRiskLevel
import com.ownlifeos.domain.model.FutureLoadForecast
import com.ownlifeos.domain.model.Reason
import com.ownlifeos.domain.model.ReasonImpact
import com.ownlifeos.domain.model.ReasonSource
import com.ownlifeos.domain.model.RecoveryAction
import com.ownlifeos.domain.model.RecoveryActionType
import com.ownlifeos.domain.model.RecoveryPlan
import com.ownlifeos.domain.model.TaskStatus
import com.ownlifeos.domain.model.TodaySystemAnalysis
import com.ownlifeos.domain.analysis.AnalysisInputs

object RecoveryPlanner {
    fun plan(
        inputs: AnalysisInputs,
        analysis: TodaySystemAnalysis,
        forecast: FutureLoadForecast
    ): RecoveryPlan {
        val trigger = forecast.highestRisk?.riskLevel ?: ForecastRiskLevel.STABLE
        val activeTasks = inputs.todayTasks.filter { it.status != TaskStatus.DONE }
        val easyTask = activeTasks.firstOrNull { it.energyCost <= 2 }
        val highRisk = trigger == ForecastRiskLevel.HIGH_RISK || analysis.stressLoad >= 70 || analysis.fatigueLoad >= 70

        val actions = buildList {
            if (highRisk || analysis.fatigueLoad >= 55) {
                add(action("10분 휴식", 10, RecoveryActionType.REST, "타이머를 10분으로 맞추고 화면에서 떨어지세요.", "피로 누적 지표를 낮추기 위한 짧은 중단입니다."))
            }
            if (analysis.stressLoad >= 55) {
                add(action("5분 정리", 5, RecoveryActionType.CLEANUP, "책상이나 작업 목록에서 눈에 보이는 항목 3개만 정리하세요.", "작업 과부하를 줄이기 위한 작은 정리입니다."))
            }
            add(action("물 마시기", 2, RecoveryActionType.HYDRATION, "자리에서 일어나 물 한 컵을 마시세요.", "낮은 비용으로 상태 전환을 만들기 위한 액션입니다."))
            if (highRisk) {
                add(action("짧은 산책", 10, RecoveryActionType.WALK, "실내 복도나 밖에서 10분만 걸으세요.", "저녁 또는 오후 부하가 커질 가능성을 낮추기 위한 이동입니다."))
            }
            if (easyTask != null) {
                add(
                    RecoveryAction(
                        title = "쉬운 작업 1개 처리",
                        durationMinutes = 15,
                        type = RecoveryActionType.EASY_TASK,
                        instruction = "'${easyTask.title}'만 작게 끝내세요.",
                        reason = Reason(
                            title = "낮은 에너지 작업",
                            detail = "에너지 ${easyTask.energyCost}점 작업으로 큐 압박을 줄일 수 있습니다.",
                            impact = ReasonImpact.MEDIUM,
                            source = ReasonSource.TASK
                        )
                    )
                )
            }
        }.take(5)

        return RecoveryPlan(
            date = inputs.date,
            triggerLevel = trigger,
            title = if (highRisk) "과부하 가능성 완화 플랜" else "가벼운 회복 플랜",
            actions = actions,
            reasons = buildReasons(analysis, forecast)
        )
    }

    private fun action(
        title: String,
        duration: Int,
        type: RecoveryActionType,
        instruction: String,
        detail: String
    ): RecoveryAction = RecoveryAction(
        title = title,
        durationMinutes = duration,
        type = type,
        instruction = instruction,
        reason = Reason(
            title = title,
            detail = detail,
            impact = ReasonImpact.MEDIUM,
            source = ReasonSource.HISTORY
        )
    )

    private fun buildReasons(
        analysis: TodaySystemAnalysis,
        forecast: FutureLoadForecast
    ): List<Reason> = buildList {
        forecast.highestRisk?.let {
            add(
                Reason(
                    title = "예측 부하",
                    detail = "${it.timeBlock.label} 위험도 ${it.riskLevel.label}, 점수 ${it.riskScore}점입니다.",
                    impact = if (it.riskLevel == ForecastRiskLevel.HIGH_RISK) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                    source = ReasonSource.HISTORY
                )
            )
        }
        add(
            Reason(
                title = "현재 부하",
                detail = "스트레스 부하 ${analysis.stressLoad}점, 피로 누적 ${analysis.fatigueLoad}점입니다.",
                impact = if (analysis.stressLoad >= 70 || analysis.fatigueLoad >= 70) ReasonImpact.HIGH else ReasonImpact.MEDIUM,
                source = ReasonSource.STRESS
            )
        )
    }
}
