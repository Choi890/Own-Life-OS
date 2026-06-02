package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.SystemHealthReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemHealthReportDao {
    @Query("SELECT * FROM system_health_reports ORDER BY generatedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 10): Flow<List<SystemHealthReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: SystemHealthReportEntity)
}
