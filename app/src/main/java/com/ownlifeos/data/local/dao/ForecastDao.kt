package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.ForecastResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast_results WHERE date = :date ORDER BY generatedAt DESC")
    fun observeByDate(date: String): Flow<List<ForecastResultEntity>>

    @Query("DELETE FROM forecast_results WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<ForecastResultEntity>)
}
