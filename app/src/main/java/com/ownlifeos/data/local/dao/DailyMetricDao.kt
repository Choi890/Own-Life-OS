package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.DailyMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMetricDao {
    @Query("SELECT * FROM daily_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeRange(startDate: String, endDate: String): Flow<List<DailyMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(metric: DailyMetricEntity)
}
