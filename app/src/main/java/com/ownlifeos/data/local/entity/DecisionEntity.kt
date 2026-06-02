package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decisions")
data class DecisionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val description: String,
    val expectedEnergyCost: Int,
    val urgency: Int,
    val reversibility: Int,
    val importance: Int,
    val predictedRiskScore: Int,
    val predictedRiskLevel: String,
    val reasons: String,
    val createdAt: Long = System.currentTimeMillis()
)
