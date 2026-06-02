package com.ownlifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ownlifeos.domain.model.TaskStatus

@Entity(tableName = "life_tasks")
data class LifeTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val importance: Int,
    val energyCost: Int,
    val status: TaskStatus = TaskStatus.WAITING,
    val deadlineDate: String? = null,
    val focusNeed: Int = 3,
    val deferredCount: Int = 0,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val lastDeferredAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
