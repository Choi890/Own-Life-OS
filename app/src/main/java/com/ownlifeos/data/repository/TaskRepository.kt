package com.ownlifeos.data.repository

import com.ownlifeos.data.local.dao.TaskDao
import com.ownlifeos.data.local.dao.TaskEventDao
import com.ownlifeos.data.local.entity.LifeTaskEntity
import com.ownlifeos.data.local.entity.TaskEventEntity
import com.ownlifeos.data.local.entity.toDomain
import com.ownlifeos.domain.model.DailyTask
import com.ownlifeos.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val dao: TaskDao,
    private val taskEventDao: TaskEventDao
) {
    fun observeByDate(date: String): Flow<List<DailyTask>> =
        dao.observeByDate(date).map { tasks -> tasks.map { it.toDomain() } }

    fun observeRange(startDate: String, endDate: String): Flow<List<DailyTask>> =
        dao.observeRange(startDate, endDate).map { tasks -> tasks.map { it.toDomain() } }

    suspend fun addTask(
        date: String,
        title: String,
        importance: Int,
        energyCost: Int,
        deadlineDate: String? = null,
        focusNeed: Int = 3
    ) {
        val taskId = dao.insert(
            LifeTaskEntity(
                date = date,
                title = title.trim(),
                importance = importance.coerceIn(1, 5),
                energyCost = energyCost.coerceIn(1, 5),
                deadlineDate = deadlineDate?.takeIf { it.isNotBlank() },
                focusNeed = focusNeed.coerceIn(1, 5)
            )
        )
        taskEventDao.insert(TaskEventEntity(taskId = taskId, date = date, eventType = "CREATED"))
    }

    suspend fun updateStatus(taskId: Long, status: TaskStatus) {
        val task = dao.getById(taskId) ?: return
        val now = System.currentTimeMillis()
        val updated = when (status) {
            TaskStatus.IN_PROGRESS -> task.copy(
                status = status,
                startedAt = task.startedAt ?: now,
                updatedAt = now
            )
            TaskStatus.DONE -> task.copy(
                status = status,
                completedAt = task.completedAt ?: now,
                updatedAt = now
            )
            TaskStatus.WAITING -> task.copy(status = status, updatedAt = now)
        }
        dao.update(updated)
        taskEventDao.insert(
            TaskEventEntity(
                taskId = taskId,
                date = task.date,
                eventType = when (status) {
                    TaskStatus.WAITING -> "WAITING"
                    TaskStatus.IN_PROGRESS -> "STARTED"
                    TaskStatus.DONE -> "COMPLETED"
                },
                createdAt = now
            )
        )
    }

    suspend fun defer(taskId: Long) {
        val task = dao.getById(taskId) ?: return
        val now = System.currentTimeMillis()
        dao.defer(taskId = taskId, deferredAt = now)
        taskEventDao.insert(
            TaskEventEntity(
                taskId = taskId,
                date = task.date,
                eventType = "DEFERRED",
                createdAt = now
            )
        )
    }

    suspend fun delete(task: DailyTask) {
        dao.delete(
            LifeTaskEntity(
                id = task.id,
                date = task.date,
                title = task.title,
                importance = task.importance,
                energyCost = task.energyCost,
                status = task.status,
                deadlineDate = task.deadlineDate,
                focusNeed = task.focusNeed,
                deferredCount = task.deferredCount,
                startedAt = task.startedAt,
                completedAt = task.completedAt,
                lastDeferredAt = task.lastDeferredAt
            )
        )
    }
}
