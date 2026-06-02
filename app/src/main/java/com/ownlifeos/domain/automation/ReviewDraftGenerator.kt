package com.ownlifeos.domain.automation

import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus

data class EveningReviewDraft(
    val summary: String,
    val goodThings: String,
    val errorLogs: String,
    val carryOver: String
) {
    val isEmpty: Boolean
        get() = goodThings.isBlank() && errorLogs.isBlank() && carryOver.isBlank()
}

object ReviewDraftGenerator {
    fun generate(tasks: List<DailyTask>): EveningReviewDraft {
        val completed = tasks.filter { it.status == TaskStatus.DONE }
        val pending = tasks.filter { it.status != TaskStatus.DONE }
        val heavyPending = pending.filter { it.energyCost >= 4 }
        val started = tasks.filter { it.status == TaskStatus.IN_PROGRESS }

        val goodThings = when {
            completed.isNotEmpty() -> "작업 ${completed.size}개를 완료했습니다.\n" +
                completed.take(3).joinToString(separator = "\n") { "- ${it.title}" }
            started.isNotEmpty() -> "진행 중인 작업을 유지했습니다.\n" +
                started.take(2).joinToString(separator = "\n") { "- ${it.title}" }
            tasks.isNotEmpty() -> "오늘 작업 큐를 정리했습니다."
            else -> "오늘 상태를 확인했습니다."
        }

        val errorLogs = when {
            pending.size >= 4 -> "미완료 작업이 ${pending.size}개 남아 있습니다. 내일은 새 작업 추가보다 기존 작업 1개를 먼저 줄이는 편이 좋습니다."
            heavyPending.isNotEmpty() -> "부담이 큰 작업 ${heavyPending.size}개가 남아 있습니다. 바로 이어서 처리하기보다 시간대를 다시 잡는 편이 좋습니다."
            completed.isEmpty() && tasks.isNotEmpty() -> "완료된 작업이 아직 없습니다. 다음 시작은 가장 작은 작업 1개로 잡는 편이 좋습니다."
            else -> "강한 반복 문제는 보이지 않습니다."
        }

        val carryOver = pending.joinToString(separator = "\n") { "- ${it.title}" }

        return EveningReviewDraft(
            summary = "하루 기록을 바탕으로 회고 초안을 만들었습니다. 맞지 않는 문장은 수정해도 됩니다.",
            goodThings = goodThings,
            errorLogs = errorLogs,
            carryOver = carryOver
        )
    }
}
