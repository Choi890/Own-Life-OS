package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.ReviewDao
import com.ownlifeos.data.local.entity.ReviewEntity
import com.ownlifeos.data.local.entity.toDomain
import com.ownlifeos.domain.model.DailyReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepository(
    private val dao: ReviewDao
) {
    fun observeByDate(date: String): Flow<DailyReview?> =
        dao.observeByDate(date).map { it?.toDomain() }

    fun observeRange(startDate: String, endDate: String): Flow<List<DailyReview>> =
        dao.observeRange(startDate, endDate).map { items -> items.map { it.toDomain() } }

    suspend fun save(
        date: String,
        goodThings: String,
        errorLogs: String,
        carryOver: String
    ) {
        dao.save(
            ReviewEntity(
                date = date,
                goodThings = goodThings.trim(),
                errorLogs = errorLogs.trim(),
                carryOver = carryOver.trim()
            )
        )
    }
}
