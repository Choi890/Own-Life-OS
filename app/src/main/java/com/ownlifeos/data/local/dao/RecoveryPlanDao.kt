package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.RecoveryPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecoveryPlanDao {
    @Query("SELECT * FROM recovery_plans WHERE date = :date ORDER BY createdAt DESC")
    fun observeByDate(date: String): Flow<List<RecoveryPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: RecoveryPlanEntity)

    @Query("UPDATE recovery_plans SET completed = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)
}
