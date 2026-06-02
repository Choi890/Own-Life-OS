package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_results")
data class ForecastResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val timeBlock: String,
    val riskLevel: String,
    val riskScore: Int,
    val summary: String,
    val reasonsJson: String,
    val generatedAt: Long = System.currentTimeMillis()
)
