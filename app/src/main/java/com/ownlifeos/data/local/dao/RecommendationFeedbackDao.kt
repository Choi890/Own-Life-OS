package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ownlifeos.data.local.entity.RecommendationFeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationFeedbackDao {
    @Insert
    suspend fun insert(feedback: RecommendationFeedbackEntity)

    @Query(
        """
        SELECT * FROM recommendation_feedback
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY createdAt DESC
        """
    )
    fun observeRange(startDate: String, endDate: String): Flow<List<RecommendationFeedbackEntity>>

    @Query(
        """
        SELECT * FROM recommendation_feedback
        WHERE date = :date AND surface = :surface
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    fun observeLatestForSurface(date: String, surface: String): Flow<RecommendationFeedbackEntity?>
}
