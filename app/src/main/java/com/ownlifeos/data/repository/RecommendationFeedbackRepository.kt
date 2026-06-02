package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.RecommendationFeedbackDao
import com.ownlifeos.data.local.entity.toEntity
import com.ownlifeos.domain.model.RecommendationFeedback
import com.ownlifeos.domain.model.RecommendationFeedbackType
import com.ownlifeos.domain.model.RecommendationSurface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecommendationFeedbackRepository(
    private val dao: RecommendationFeedbackDao
) {
    fun observeRange(startDate: String, endDate: String): Flow<List<RecommendationFeedback>> =
        dao.observeRange(startDate, endDate).map { feedbacks -> feedbacks.map { it.toDomain() } }

    fun observeLatestForSurface(
        date: String,
        surface: RecommendationSurface
    ): Flow<RecommendationFeedback?> =
        dao.observeLatestForSurface(date, surface.name).map { it?.toDomain() }

    suspend fun record(
        date: String,
        surface: RecommendationSurface,
        feedbackType: RecommendationFeedbackType,
        note: String = ""
    ) {
        dao.insert(
            RecommendationFeedback(
                id = 0,
                date = date,
                surface = surface,
                feedbackType = feedbackType,
                note = note
            ).toEntity()
        )
    }
}
