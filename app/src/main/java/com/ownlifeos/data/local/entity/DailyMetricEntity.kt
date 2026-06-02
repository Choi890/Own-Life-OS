package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_metrics")
data class DailyMetricEntity(
    @PrimaryKey val date: String,
    val lifeBattery: Int,
    val focusLevel: Int,
    val stressLoad: Int,
    val fatigueLoad: Int,
    val todayMode: String,
    val reasonSummary: String,
    val generatedAt: Long = System.currentTimeMillis()
)
