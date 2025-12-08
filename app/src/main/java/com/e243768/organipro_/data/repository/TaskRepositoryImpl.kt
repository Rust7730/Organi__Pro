package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.TaskDao
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.model.TaskStatus
import com.e243768.organipro_.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirebaseFirestoreService
) : TaskRepository {
    override suspend fun getTaskById(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskByIdFlow(taskId: String): Flow<Task?> {
        return flowOf(null) // Devuelve null de forma segura
    }

    override suspend fun getTasksByUserId(userId: String): Flow<List<Task>> {
        return flowOf(emptyList()) // Devuelve lista vacía
    }

    override suspend fun createTask(task: Task): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<Task>> {
        return flowOf(emptyList())
    }

    override suspend fun getTasksByPriority(userId: String, priority: Priority): Flow<List<Task>> {
        return flowOf(emptyList())
    }

    override suspend fun getTasksByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Task>> {
        return flowOf(emptyList())
    }

    override suspend fun getCompletedTasks(userId: String): Flow<List<Task>> {
        return flowOf(emptyList())
    }

    override suspend fun getOverdueTasks(userId: String): Flow<List<Task>> {
        return flowOf(emptyList())
    }

    override suspend fun getTodayTasks(userId: String): Flow<List<Task>> {
        return flowOf(emptyList()) // Devuelve lista vacía
    }

    override suspend fun getWeekTasks(userId: String): Flow<List<Task>> {
        return flowOf(emptyList()) // Devuelve lista vacía
    }

    override suspend fun getMonthTasks(userId: String): Flow<List<Task>> {
        return flowOf(emptyList()) // Devuelve lista vacía
    }

    override suspend fun markTaskAsCompleted(taskId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun markTaskAsInProgress(taskId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTaskProgress(taskId: String, progress: Float): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchTasksFromRemote(userId: String): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncTask(task: Task): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun syncAllTasks(userId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedTasksCount(userId: String): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksCreatedToday(userId: String): Result<Int> {
        TODO("Not yet implemented")
    }

}