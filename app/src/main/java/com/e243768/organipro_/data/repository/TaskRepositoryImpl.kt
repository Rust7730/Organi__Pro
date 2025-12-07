package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.util.DateUtils
import com.e243768.organipro_.data.local.dao.AttachmentDao
import com.e243768.organipro_.data.local.dao.TaskDao
import com.e243768.organipro_.data.local.entities.TaskEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.TaskMapper
import com.e243768.organipro_.domain.model.*
import com.e243768.organipro_.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val attachmentDao: AttachmentDao,
    private val firestoreService: FirebaseFirestoreService
) : TaskRepository {

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            // Intentar obtener de local primero
            val taskEntity = taskDao.getTaskById(taskId)
            if (taskEntity != null) {
                val attachments = attachmentDao.getAttachmentsByTaskId(taskId).map { it.toDomain() }
                val task = taskEntity.toDomain().copy(attachments = attachments)
                return Result.Success(task)
            }

            // Si no está en local, obtener de Firebase
            val taskMap = firestoreService.getDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = taskId,
                clazz = Map::class.java
            ) as? Map<String, Any?> ?: return Result.Error("Tarea no encontrada")

            val task = TaskMapper.fromFirebaseMap(taskMap)

            // Guardar en local
            createTask(task)

            Result.Success(task)
        } catch (e: Exception) {
            Result.Error("Error al obtener tarea: ${e.message}", e)
        }
    }

    override fun getTaskByIdFlow(taskId: String): Flow<Task?> {
        return taskDao.getTaskByIdFlow(taskId).map { taskEntity ->
            taskEntity?.let {
                val attachments = attachmentDao.getAttachmentsByTaskId(taskId).map { it.toDomain() }
                it.toDomain().copy(attachments = attachments)
            }
        }
    }

    override suspend fun getTasksByUserId(userId: String): Flow<List<Task>> {
        return taskDao.getTasksByUserId(userId).map { entities ->
            entities.map { entity ->
                val attachments = attachmentDao.getAttachmentsByTaskId(entity.id).map { it.toDomain() }
                entity.toDomain().copy(attachments = attachments)
            }
        }
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            // 1. Guardar en local
            val taskEntity = TaskEntity.fromDomain(task, synced = false)
            taskDao.insertTask(taskEntity)

            // 2. Subir a Firebase
            val taskMap = TaskMapper.toFirebaseMap(task)
            firestoreService.setDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = task.id,
                data = taskMap
            )

            // 3. Marcar como sincronizado
            taskDao.updateSyncStatus(task.id, synced = true)

            Result.Success(task)
        } catch (e: Exception) {
            Result.Error("Error al crear tarea: ${e.message}", e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val updatedTask = task.copy(updatedAt = Date())

            // 1. Actualizar local
            val taskEntity = TaskEntity.fromDomain(updatedTask, synced = false)
            taskDao.updateTask(taskEntity)

            // 2. Actualizar Firebase
            val taskMap = TaskMapper.toFirebaseMap(updatedTask)
            firestoreService.setDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = task.id,
                data = taskMap,
                merge = true
            )

            // 3. Marcar como sincronizado
            taskDao.updateSyncStatus(task.id, synced = true)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar tarea: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            // 1. Eliminar de local
            taskDao.deleteTaskById(taskId)

            // 2. Eliminar de Firebase
            firestoreService.deleteDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = taskId
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar tarea: ${e.message}", e)
        }
    }

    override suspend fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(userId, status).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTasksByPriority(userId: String, priority: Priority): Flow<List<Task>> {
        return taskDao.getTasksByPriority(userId, priority).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTasksByDateRange(
        userId: String,
        startDate: Date,
        endDate: Date
    ): Flow<List<Task>> {
        return taskDao.getTasksByDateRange(userId, startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCompletedTasks(userId: String): Flow<List<Task>> {
        return taskDao.getCompletedTasks(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getOverdueTasks(userId: String): Flow<List<Task>> {
        val currentDate = Date()
        return taskDao.getOverdueTasks(userId, currentDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTodayTasks(userId: String): Flow<List<Task>> {
        val startOfDay = DateUtils.getStartOfDay(Date())
        val endOfDay = DateUtils.getEndOfDay(Date())
        return getTasksByDateRange(userId, startOfDay, endOfDay)
    }

    override suspend fun getWeekTasks(userId: String): Flow<List<Task>> {
        val startOfWeek = DateUtils.getStartOfWeek(Date())
        val endOfWeek = DateUtils.getEndOfWeek(Date())
        return getTasksByDateRange(userId, startOfWeek, endOfWeek)
    }

    override suspend fun getMonthTasks(userId: String): Flow<List<Task>> {
        val startOfMonth = DateUtils.getStartOfMonth(Date())
        val endOfMonth = DateUtils.getEndOfMonth(Date())
        return getTasksByDateRange(userId, startOfMonth, endOfMonth)
    }

    override suspend fun markTaskAsCompleted(taskId: String): Result<Unit> {
        return try {
            val completedAt = Date()

            // 1. Actualizar local
            taskDao.markTaskAsCompleted(taskId, TaskStatus.COMPLETED, completedAt)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = taskId,
                updates = mapOf(
                    "status" to TaskStatus.COMPLETED.name,
                    "completedAt" to com.google.firebase.Timestamp(completedAt),
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al completar tarea: ${e.message}", e)
        }
    }

    override suspend fun markTaskAsInProgress(taskId: String): Result<Unit> {
        return try {
            val taskResult = getTaskById(taskId)
            if (taskResult is Result.Success) {
                val task = taskResult.data.copy(status = TaskStatus.IN_PROGRESS)
                updateTask(task)
            } else {
                Result.Error("Tarea no encontrada")
            }
        } catch (e: Exception) {
            Result.Error("Error al marcar tarea en progreso: ${e.message}", e)
        }
    }

    override suspend fun updateTaskProgress(taskId: String, progress: Float): Result<Unit> {
        return try {
            // 1. Actualizar local
            taskDao.updateTaskProgress(taskId, progress)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_TASKS,
                documentId = taskId,
                updates = mapOf(
                    "progress" to progress,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar progreso: ${e.message}", e)
        }
    }

    override suspend fun fetchTasksFromRemote(userId: String): Result<List<Task>> {
        return try {
            val taskMaps = firestoreService.getDocuments(
                collection = FirebaseConstants.COLLECTION_TASKS,
                clazz = Map::class.java
            ) { query ->
                query.whereEqualTo("userId", userId)
            } as List<Map<String, Any?>>

            val tasks = taskMaps.map { TaskMapper.fromFirebaseMap(it) }

            // Guardar en local
            val taskEntities = tasks.map { TaskEntity.fromDomain(it, synced = true) }
            taskDao.insertTasks(taskEntities)

            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error("Error al obtener tareas de Firebase: ${e.message}", e)
        }
    }

    override suspend fun syncTask(task: Task): Result<Unit> {
        return try {
            createTask(task)
            taskDao.updateSyncStatus(task.id, synced = true)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar tarea: ${e.message}", e)
        }
    }

    override suspend fun syncAllTasks(userId: String): Result<Unit> {
        return try {
            val unsyncedTasks = taskDao.getUnsyncedTasks()

            unsyncedTasks.forEach { entity ->
                val task = entity.toDomain()
                val taskMap = TaskMapper.toFirebaseMap(task)

                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_TASKS,
                    documentId = entity.id,
                    data = taskMap,
                    merge = true
                )

                taskDao.updateSyncStatus(entity.id, synced = true)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar tareas: ${e.message}", e)
        }
    }

    override suspend fun getCompletedTasksCount(userId: String): Result<Int> {
        return try {
            val count = taskDao.getCompletedTasksCount(userId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error("Error al obtener conteo de tareas: ${e.message}", e)
        }
    }

    override suspend fun getTasksCreatedToday(userId: String): Result<Int> {
        return try {
            val startOfDay = DateUtils.getStartOfDay(Date())
            val endOfDay = DateUtils.getEndOfDay(Date())
            // Esta query necesitaría agregarse al DAO
            Result.Success(0)
        } catch (e: Exception) {
            Result.Error("Error al obtener tareas creadas hoy: ${e.message}", e)
        }
    }
}