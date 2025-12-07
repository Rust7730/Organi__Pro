package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.Attachment
import kotlinx.coroutines.flow.Flow

interface AttachmentRepository {

    // CRUD operations
    suspend fun getAttachmentById(attachmentId: String): Result<Attachment>
    suspend fun getAttachmentsByTaskId(taskId: String): Result<List<Attachment>>
    fun getAttachmentsByTaskIdFlow(taskId: String): Flow<List<Attachment>>
    suspend fun addAttachment(attachment: Attachment): Result<Attachment>
    suspend fun deleteAttachment(attachmentId: String): Result<Unit>
    suspend fun deleteAttachmentsByTaskId(taskId: String): Result<Unit>

    // Upload/Download
    suspend fun uploadAttachment(attachment: Attachment, localFilePath: String): Result<Attachment>
    suspend fun downloadAttachment(attachment: Attachment): Result<String>
    suspend fun getPendingUploads(): Result<List<Attachment>>
    suspend fun syncPendingUploads(): Result<Unit>

    // Utility
    suspend fun updateUploadStatus(attachmentId: String, isUploaded: Boolean, url: String): Result<Unit>
}