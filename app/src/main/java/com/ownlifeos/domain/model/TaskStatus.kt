package com.ownlifeos.domain.model

enum class TaskStatus(val label: String) {
    WAITING("대기"),
    IN_PROGRESS("진행"),
    DONE("완료")
}
