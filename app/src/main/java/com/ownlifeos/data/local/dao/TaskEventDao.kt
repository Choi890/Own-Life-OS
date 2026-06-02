package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.TaskEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskEventDao {
    @Query("SELECT * FROM task_events WHERE date BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun observeRange(startDate: String, endDate: String): Flow<List<TaskEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: TaskEventEntity)
}
