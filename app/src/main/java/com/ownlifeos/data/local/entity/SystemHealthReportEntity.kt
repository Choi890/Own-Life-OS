package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_health_reports")
data class SystemHealthReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekStartDate: String,
    val weekEndDate: String,
    val healthScore: Int,
    val stableAreasJson: String,
    val unstableAreasJson: String,
    val nextWeekStrategiesJson: String,
    val reasonsJson: String,
    val generatedAt: Long = System.currentTimeMillis()
)
