package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.TaskEntity
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskByIdFlow(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY due_date ASC")
    fun getTasksByUserId(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND status = :status ORDER BY due_date ASC")
    fun getTasksByStatus(userId: String, status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND priority = :priority ORDER BY due_date ASC")
    fun getTasksByPriority(userId: String, priority: Priority): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND due_date >= :startDate AND due_date <= :endDate ORDER BY due_date ASC")
    fun getTasksByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND status = 'COMPLETED' ORDER BY completed_at DESC")
    fun getCompletedTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND status != 'COMPLETED' AND due_date < :currentDate ORDER BY due_date ASC")
    fun getOverdueTasks(userId: String, currentDate: Date): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("UPDATE tasks SET status = :status, completed_at = :completedAt WHERE id = :taskId")
    suspend fun markTaskAsCompleted(taskId: String, status: TaskStatus, completedAt: Date)

    @Query("UPDATE tasks SET progress = :progress WHERE id = :taskId")
    suspend fun updateTaskProgress(taskId: String, progress: Float)

    @Query("UPDATE tasks SET synced = :synced WHERE id = :taskId")
    suspend fun updateSyncStatus(taskId: String, synced: Boolean)

    @Query("SELECT * FROM tasks WHERE synced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks WHERE user_id = :userId AND status = 'COMPLETED'")
    suspend fun getCompletedTasksCount(userId: String): Int

    @Query("DELETE FROM tasks WHERE user_id = :userId")
    suspend fun deleteAllTasksByUserId(userId: String)
}