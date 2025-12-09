package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.TaskDao
import com.e243768.organipro_.data.local.entities.TaskEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.model.TaskStatus
import com.e243768.organipro_.domain.repository.TaskRepository
import com.e243768.organipro_.core.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirebaseFirestoreService
) : TaskRepository {

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            val entity = taskDao.getTaskById(taskId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error("Tarea no encontrada")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener tarea", e)
        }
    }

    override fun getTaskByIdFlow(taskId: String): Flow<Task?> {
        return taskDao.getTaskByIdFlow(taskId).map { it?.toDomain() }
    }

    override suspend fun getTasksByUserId(userId: String): Flow<List<Task>> {
        return taskDao.getTasksByUserId(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    // --- AQUÍ ESTABA EL CRASH (createTask) ---
    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            // Convertimos el modelo de Dominio a Entidad de Base de Datos
            val entity = TaskEntity.fromDomain(task)
            // Insertamos en Room
            taskDao.insertTask(entity)
            // Retornamos éxito
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error("Error al guardar la tarea: ${e.localizedMessage}", e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val entity = TaskEntity.fromDomain(task)
            taskDao.updateTask(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar tarea", e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            taskDao.deleteTaskById(taskId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar tarea", e)
        }
    }

    override suspend fun getTodayTasks(userId: String): Flow<List<Task>> {
        val startOfDay = DateUtils.getStartOfDay()
        val endOfDay = DateUtils.getEndOfDay()
        return taskDao.getTasksByDateRange(userId, startOfDay, endOfDay)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getWeekTasks(userId: String): Flow<List<Task>> {
        val startOfWeek = DateUtils.getStartOfWeek()
        val endOfWeek = DateUtils.getEndOfWeek()
        return taskDao.getTasksByDateRange(userId, startOfWeek, endOfWeek)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getMonthTasks(userId: String): Flow<List<Task>> {
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()
        return taskDao.getTasksByDateRange(userId, startOfMonth, endOfMonth)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun markTaskAsCompleted(taskId: String): Result<Unit> {
        return try {
            taskDao.markTaskAsCompleted(taskId, TaskStatus.COMPLETED, Date())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al completar tarea", e)
        }
    }

    // Implementaciones vacías seguras para métodos opcionales o futuros
    override suspend fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<Task>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun getTasksByPriority(userId: String, priority: Priority): Flow<List<Task>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun getTasksByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Task>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun getCompletedTasks(userId: String): Flow<List<Task>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun getOverdueTasks(userId: String): Flow<List<Task>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun markTaskAsInProgress(taskId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun updateTaskProgress(taskId: String, progress: Float): Result<Unit> = Result.Success(Unit)
    override suspend fun fetchTasksFromRemote(userId: String): Result<List<Task>> = Result.Success(emptyList())
    override suspend fun syncTask(task: Task): Result<Unit> = Result.Success(Unit)
    override suspend fun syncAllTasks(userId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun getCompletedTasksCount(userId: String): Result<Int> = Result.Success(0)
    override suspend fun getTasksCreatedToday(userId: String): Result<Int> = Result.Success(0)
}