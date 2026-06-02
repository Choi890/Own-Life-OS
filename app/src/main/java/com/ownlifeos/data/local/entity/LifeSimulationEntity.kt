package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_simulations")
data class LifeSimulationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val scenarioTitle: String,
    val scenarioDescription: String,
    val completionPossibility: String,
    val stressImpact: String,
    val regretPossibility: String,
    val completionScore: Int,
    val stressScore: Int,
    val regretScore: Int,
    val summary: String,
    val reasonsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
