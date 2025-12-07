package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.AttachmentDao
import com.e243768.organipro_.data.local.entities.AttachmentEntity
import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttachmentRepositoryImpl(
    private val attachmentDao: AttachmentDao
    // private val firebaseStorage: FirebaseStorage // TODO: Agregar cuando tengamos Firebase
) : AttachmentRepository {

    override suspend fun getAttachmentById(attachmentId: String): Result<Attachment> {
        return try {
            val entity = attachmentDao.getAttachmentById(attachmentId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error("Archivo no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener archivo: ${e.message}", e)
        }
    }

    override suspend fun getAttachmentsByTaskId(taskId: String): Result<List<Attachment>> {
        return try {
            val entities = attachmentDao.getAttachmentsByTaskId(taskId)
            Result.Success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error("Error al obtener archivos: ${e.message}", e)
        }
    }

    override fun getAttachmentsByTaskIdFlow(taskId: String): Flow<List<Attachment>> {
        return attachmentDao.getAttachmentsByTaskIdFlow(taskId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addAttachment(attachment: Attachment): Result<Attachment> {
        return try {
            val entity = AttachmentEntity.fromDomain(attachment, synced = false)
            attachmentDao.insertAttachment(entity)
            Result.Success(attachment)
        } catch (e: Exception) {
            Result.Error("Error al agregar archivo: ${e.message}", e)
        }
    }

    override suspend fun deleteAttachment(attachmentId: String): Result<Unit> {
        return try {
            attachmentDao.deleteAttachmentById(attachmentId)
            // TODO: Eliminar de Firebase Storage también
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar archivo: ${e.message}", e)
        }
    }

    override suspend fun deleteAttachmentsByTaskId(taskId: String): Result<Unit> {
        return try {
            attachmentDao.deleteAttachmentsByTaskId(taskId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar archivos: ${e.message}", e)
        }
    }

    override suspend fun uploadAttachment(
        attachment: Attachment,
        localFilePath: String
    ): Result<Attachment> {
        // TODO: Implementar cuando tengamos Firebase Storage
        return try {
            // 1. Subir archivo a Firebase Storage
            // val storageRef = firebaseStorage.reference.child("attachments/${attachment.id}")
            // val uploadTask = storageRef.putFile(Uri.fromFile(File(localFilePath)))
            // val downloadUrl = uploadTask.await().storage.downloadUrl.await()

            // 2. Actualizar URL en la base de datos local
            val updatedAttachment = attachment.copy(
                url = "https://firebase-url.com/${attachment.id}", // URL temporal
                isUploaded = true
            )

            val entity = AttachmentEntity.fromDomain(updatedAttachment, synced = true)
            attachmentDao.updateAttachment(entity)

            Result.Success(updatedAttachment)
        } catch (e: Exception) {
            Result.Error("Error al subir archivo: ${e.message}", e)
        }
    }

    override suspend fun downloadAttachment(attachment: Attachment): Result<String> {
        // TODO: Implementar cuando tengamos Firebase Storage
        return try {
            // 1. Descargar desde Firebase Storage
            // 2. Guardar en almacenamiento local
            // 3. Devolver ruta local
            Result.Success(attachment.localPath ?: "")
        } catch (e: Exception) {
            Result.Error("Error al descargar archivo: ${e.message}", e)
        }
    }

    override suspend fun getPendingUploads(): Result<List<Attachment>> {
        return try {
            val entities = attachmentDao.getPendingUploads()
            Result.Success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error("Error al obtener pendientes: ${e.message}", e)
        }
    }

    override suspend fun syncPendingUploads(): Result<Unit> {
        return try {
            val pendingResult = getPendingUploads()
            if (pendingResult is Result.Success) {
                pendingResult.data.forEach { attachment ->
                    attachment.localPath?.let { localPath ->
                        uploadAttachment(attachment, localPath)
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar archivos: ${e.message}", e)
        }
    }

    override suspend fun updateUploadStatus(
        attachmentId: String,
        isUploaded: Boolean,
        url: String
    ): Result<Unit> {
        return try {
            attachmentDao.updateUploadStatus(attachmentId, isUploaded, url)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar estado: ${e.message}", e)
        }
    }
}