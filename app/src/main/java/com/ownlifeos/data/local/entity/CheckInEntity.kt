package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "check_ins",
    indices = [Index(value = ["date"], unique = true)]
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val sleepHours: Double,
    val mood: Int,
    val bodyCondition: Int,
    val burdenLevel: Int,
    val memo: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
