package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM attachments WHERE id = :attachmentId")
    suspend fun getAttachmentById(attachmentId: String): AttachmentEntity?

    @Query("SELECT * FROM attachments WHERE task_id = :taskId")
    suspend fun getAttachmentsByTaskId(taskId: String): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE task_id = :taskId")
    fun getAttachmentsByTaskIdFlow(taskId: String): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    @Update
    suspend fun updateAttachment(attachment: AttachmentEntity)

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Query("DELETE FROM attachments WHERE id = :attachmentId")
    suspend fun deleteAttachmentById(attachmentId: String)

    @Query("DELETE FROM attachments WHERE task_id = :taskId")
    suspend fun deleteAttachmentsByTaskId(taskId: String)

    @Query("UPDATE attachments SET is_uploaded = :isUploaded, url = :url WHERE id = :attachmentId")
    suspend fun updateUploadStatus(attachmentId: String, isUploaded: Boolean, url: String)

    @Query("SELECT * FROM attachments WHERE is_uploaded = 0")
    suspend fun getPendingUploads(): List<AttachmentEntity>
}