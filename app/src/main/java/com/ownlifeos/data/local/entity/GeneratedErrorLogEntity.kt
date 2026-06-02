package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_error_logs")
data class GeneratedErrorLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val type: String,
    val severity: String,
    val title: String,
    val detail: String,
    val evidence: String,
    val createdAt: Long = System.currentTimeMillis(),
    val dismissed: Boolean = false
)
