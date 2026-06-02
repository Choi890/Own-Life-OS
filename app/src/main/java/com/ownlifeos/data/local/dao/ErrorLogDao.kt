package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.GeneratedErrorLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErrorLogDao {
    @Query("SELECT * FROM generated_error_logs WHERE date = :date AND dismissed = 0 ORDER BY createdAt DESC")
    fun observeActiveByDate(date: String): Flow<List<GeneratedErrorLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(logs: List<GeneratedErrorLogEntity>)

    @Query("UPDATE generated_error_logs SET dismissed = 1 WHERE id = :id")
    suspend fun dismiss(id: Long)
}
