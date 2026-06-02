package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_outcomes")
data class DecisionOutcomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val decisionId: Long,
    val actualResult: String,
    val regretLevel: Int,
    val energyImpact: Int,
    val note: String,
    val recordedAt: Long = System.currentTimeMillis()
)
