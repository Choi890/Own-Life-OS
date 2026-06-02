package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ownlifeos.data.local.entity.LifeTaskEntity
import com.ownlifeos.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT * FROM life_tasks
        WHERE date = :date
        ORDER BY
            CASE status
                WHEN 'IN_PROGRESS' THEN 0
                WHEN 'WAITING' THEN 1
                ELSE 2
            END,
            importance DESC,
            energyCost DESC,
            createdAt ASC
        """
    )
    fun observeByDate(date: String): Flow<List<LifeTaskEntity>>

    @Query("SELECT * FROM life_tasks WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt ASC")
    fun observeRange(startDate: String, endDate: String): Flow<List<LifeTaskEntity>>

    @Query("SELECT * FROM life_tasks WHERE id = :taskId LIMIT 1")
    suspend fun getById(taskId: Long): LifeTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: LifeTaskEntity): Long

    @Update
    suspend fun update(task: LifeTaskEntity)

    @Delete
    suspend fun delete(task: LifeTaskEntity)

    @Query("UPDATE life_tasks SET status = :status, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateStatus(taskId: Long, status: TaskStatus, updatedAt: Long = System.currentTimeMillis())

    @Query(
        """
        UPDATE life_tasks
        SET deferredCount = deferredCount + 1,
            lastDeferredAt = :deferredAt,
            updatedAt = :deferredAt
        WHERE id = :taskId
        """
    )
    suspend fun defer(taskId: Long, deferredAt: Long = System.currentTimeMillis())
}
