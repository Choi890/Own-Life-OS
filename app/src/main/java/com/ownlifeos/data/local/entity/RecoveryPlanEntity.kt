package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recovery_plans")
data class RecoveryPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val triggerLevel: String,
    val title: String,
    val actionsJson: String,
    val reasonsJson: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
