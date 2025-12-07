package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskRepository {

    // CRUD operations
    suspend fun getTaskById(taskId: String): Result<Task>
    fun getTaskByIdFlow(taskId: String): Flow<Task?>
    suspend fun getTasksByUserId(userId: String): Flow<List<Task>>
    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>

    // Filter operations
    suspend fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<Task>>
    suspend fun getTasksByPriority(userId: String, priority: Priority): Flow<List<Task>>
    suspend fun getTasksByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Task>>
    suspend fun getCompletedTasks(userId: String): Flow<List<Task>>
    suspend fun getOverdueTasks(userId: String): Flow<List<Task>>
    suspend fun getTodayTasks(userId: String): Flow<List<Task>>
    suspend fun getWeekTasks(userId: String): Flow<List<Task>>
    suspend fun getMonthTasks(userId: String): Flow<List<Task>>

    // Task completion
    suspend fun markTaskAsCompleted(taskId: String): Result<Unit>
    suspend fun markTaskAsInProgress(taskId: String): Result<Unit>
    suspend fun updateTaskProgress(taskId: String, progress: Float): Result<Unit>

    // Remote sync
    suspend fun fetchTasksFromRemote(userId: String): Result<List<Task>>
    suspend fun syncTask(task: Task): Result<Unit>
    suspend fun syncAllTasks(userId: String): Result<Unit>

    // Statistics
    suspend fun getCompletedTasksCount(userId: String): Result<Int>
    suspend fun getTasksCreatedToday(userId: String): Result<Int>
}