package com.ownlifeos.data.local.converter

import androidx.room.TypeConverter
import com.ownlifeos.domain.model.TaskStatus

class TaskStatusConverter {
    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
}
