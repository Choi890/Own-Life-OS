package com.ownlifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ownlifeos.data.local.entity.LifeSimulationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeSimulationDao {
    @Query("SELECT * FROM life_simulations ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<LifeSimulationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(simulation: LifeSimulationEntity)
}
