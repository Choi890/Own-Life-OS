package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<CheckInEntity?>

    @Query("SELECT * FROM check_ins WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeRange(startDate: String, endDate: String): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(checkIn: CheckInEntity)
}
