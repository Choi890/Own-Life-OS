package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.RecommendationSurface

@Entity(tableName = "recommendation_feedback")
data class RecommendationFeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val surface: String,
    val feedbackType: String,
    val note: String,
    val createdAt: Long
) {
    fun toDomain(): RecommendationFeedback = RecommendationFeedback(
        id = id,
        date = date,
        surface = runCatching { RecommendationSurface.valueOf(surface) }
            .getOrDefault(RecommendationSurface.DAILY_STRATEGY),
        feedbackType = runCatching { RecommendationFeedbackType.valueOf(feedbackType) }
            .getOrDefault(RecommendationFeedbackType.MATCHED),
        note = note,
        createdAt = createdAt
    )
}

fun RecommendationFeedback.toEntity(): RecommendationFeedbackEntity = RecommendationFeedbackEntity(
    id = id,
    date = date,
    surface = surface.name,
    feedbackType = feedbackType.name,
    note = note,
    createdAt = createdAt
)
