package com.ownlifeos.domain.automation

data class TaskBurdenEstimate(
    val energyCost: Int,
    val importance: Int,
    val focusNeed: Int,
    val label: String,
    val reason: String
)

object TaskBurdenEstimator {
    fun estimate(title: String): TaskBurdenEstimate {
        val normalized = title.lowercase()
        val heavyHit = heavyKeywords.firstOrNull { normalized.contains(it) }
        val mediumHit = mediumKeywords.firstOrNull { normalized.contains(it) }
        val lightHit = lightKeywords.firstOrNull { normalized.contains(it) }

        return when {
            title.isBlank() -> medium("제목을 입력하면 부담도를 자동으로 다시 추정합니다.")
            heavyHit != null -> heavy("'$heavyHit' 키워드가 있어 큰 작업으로 추정했습니다.")
            mediumHit != null -> medium("'$mediumHit' 키워드가 있어 보통 작업으로 추정했습니다.")
            lightHit != null -> light("'$lightHit' 키워드가 있어 가벼운 작업으로 추정했습니다.")
            title.length <= 8 -> light("짧은 단일 작업처럼 보여 가벼운 작업으로 추정했습니다.")
            else -> medium("명확한 키워드가 없어 보통 작업으로 시작합니다.")
        }
    }

    fun labelForEnergy(energyCost: Int): String = when {
        energyCost <= 2 -> "가벼움"
        energyCost >= 4 -> "무거움"
        else -> "보통"
    }

    private fun light(reason: String) = TaskBurdenEstimate(
        energyCost = 1,
        importance = 2,
        focusNeed = 2,
        label = "가벼움",
        reason = reason
    )

    private fun medium(reason: String) = TaskBurdenEstimate(
        energyCost = 3,
        importance = 3,
        focusNeed = 3,
        label = "보통",
        reason = reason
    )

    private fun heavy(reason: String) = TaskBurdenEstimate(
        energyCost = 5,
        importance = 4,
        focusNeed = 4,
        label = "무거움",
        reason = reason
    )

    private val lightKeywords = listOf(
        "답장",
        "확인",
        "제출",
        "예약",
        "연락",
        "전화",
        "문자",
        "메일",
        "이메일",
        "보내기"
    )
    private val mediumKeywords = listOf(
        "공부",
        "복습",
        "수정",
        "작성",
        "준비",
        "정리",
        "자료",
        "문서"
    )
    private val heavyKeywords = listOf(
        "구현",
        "개발",
        "전체",
        "완성",
        "발표",
        "프로젝트",
        "시험",
        "과제",
        "보고서",
        "청소"
    )
}
