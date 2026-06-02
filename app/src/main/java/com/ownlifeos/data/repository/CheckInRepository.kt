package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.CheckInDao
import com.ownlifeos.data.local.entity.CheckInEntity
import com.ownlifeos.data.local.entity.toDomain
import com.ownlifeos.domain.model.DailyCheckIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CheckInRepository(
    private val dao: CheckInDao
) {
    fun observeByDate(date: String): Flow<DailyCheckIn?> =
        dao.observeByDate(date).map { it?.toDomain() }

    fun observeRange(startDate: String, endDate: String): Flow<List<DailyCheckIn>> =
        dao.observeRange(startDate, endDate).map { items -> items.map { it.toDomain() } }

    suspend fun save(
        date: String,
        sleepHours: Double,
        mood: Int,
        bodyCondition: Int,
        burdenLevel: Int,
        memo: String
    ) {
        dao.save(
            CheckInEntity(
                date = date,
                sleepHours = sleepHours,
                mood = mood.coerceIn(1, 5),
                bodyCondition = bodyCondition.coerceIn(1, 5),
                burdenLevel = burdenLevel.coerceIn(1, 5),
                memo = memo.trim()
            )
        )
    }
}
