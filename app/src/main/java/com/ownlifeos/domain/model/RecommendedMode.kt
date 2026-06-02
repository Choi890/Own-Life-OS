package com.ownlifeos.domain.model

enum class RecommendedMode(
    val title: String,
    val description: String
) {
    RECOVERY(
        title = "저전력 회복 모드",
        description = "큰 결정보다 회복, 정리, 짧은 작업을 우선하세요."
    ),
    DEEP_FOCUS(
        title = "집중 실행 모드",
        description = "가장 중요한 작업 하나를 먼저 처리하기 좋은 상태입니다."
    ),
    CLEAR_QUEUE(
        title = "큐 정리 모드",
        description = "작업을 줄이고, 넘길 일과 오늘 끝낼 일을 분리하세요."
    ),
    STEADY(
        title = "안정 운용 모드",
        description = "무리하지 않고 계획한 루틴을 일정하게 유지하세요."
    ),
    REVIEW(
        title = "회고 저장 모드",
        description = "오늘의 로그를 닫고 내일로 넘길 항목을 정리하세요."
    )
}
