package com.e243768.organipro_.data.repository

import android.net.Uri
import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.AttachmentDao
import com.e243768.organipro_.data.local.entities.AttachmentEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.firebase.FirebaseStorageService
import com.e243768.organipro_.data.remote.mappers.AttachmentMapper
import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val firestoreService: FirebaseFirestoreService,
    private val storageService: FirebaseStorageService
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
            // 1. Guardar en local
            val entity = AttachmentEntity.fromDomain(attachment, synced = false)
            attachmentDao.insertAttachment(entity)

            // 2. Guardar metadata en Firestore
            val attachmentMap = AttachmentMapper.toFirebaseMap(attachment)
            firestoreService.setDocument(
                collection = "${FirebaseConstants.COLLECTION_TASKS}/${attachment.taskId}/${FirebaseConstants.COLLECTION_ATTACHMENTS}",
                documentId = attachment.id,
                data = attachmentMap
            )

            Result.Success(attachment)
        } catch (e: Exception) {
            Result.Error("Error al agregar archivo: ${e.message}", e)
        }
    }

    override suspend fun deleteAttachment(attachmentId: String): Result<Unit> {
        return try {
            val attachmentResult = getAttachmentById(attachmentId)
            if (attachmentResult is Result.Success) {
                val attachment = attachmentResult.data

                // 1. Eliminar de local
                attachmentDao.deleteAttachmentById(attachmentId)

                // 2. Eliminar de Firebase Storage
                if (attachment.isUploaded) {
                    // Extraer path del URL o usar localPath
                    // storageService.deleteFile(path)
                }

                // 3. Eliminar metadata de Firestore
                firestoreService.deleteDocument(
                    collection = "${FirebaseConstants.COLLECTION_TASKS}/${attachment.taskId}/${FirebaseConstants.COLLECTION_ATTACHMENTS}",
                    documentId = attachmentId
                )
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar archivo: ${e.message}", e)
        }
    }

    override suspend fun deleteAttachmentsByTaskId(taskId: String): Result<Unit> {
        return try {
            val attachmentsResult = getAttachmentsByTaskId(taskId)
            if (attachmentsResult is Result.Success) {
                attachmentsResult.data.forEach { attachment ->
                    deleteAttachment(attachment.id)
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar archivos: ${e.message}", e)
        }
    }

    override suspend fun uploadAttachment(
        attachment: Attachment,
        localFilePath: String
    ): Result<Attachment> {
        return try {
            // 1. Generar path en Storage
            val storagePath = storageService.generateAttachmentPath(
                userId = "userId", // Obtener del contexto o parámetro
                taskId = attachment.taskId,
                fileName = attachment.name
            )

            // 2. Subir archivo a Firebase Storage
            val downloadUrl = storageService.uploadFile(
                path = storagePath,
                fileUri = Uri.fromFile(File(localFilePath))
            )

            // 3. Actualizar attachment con URL
            val updatedAttachment = attachment.copy(
                url = downloadUrl,
                isUploaded = true
            )

            // 4. Actualizar en local
            val entity = AttachmentEntity.fromDomain(updatedAttachment, synced = true)
            attachmentDao.updateAttachment(entity)

            // 5. Actualizar metadata en Firestore
            firestoreService.updateDocument(
                collection = "${FirebaseConstants.COLLECTION_TASKS}/${attachment.taskId}/${FirebaseConstants.COLLECTION_ATTACHMENTS}",
                documentId = attachment.id,
                updates = mapOf(
                    "url" to downloadUrl,
                    "isUploaded" to true
                )
            )

            Result.Success(updatedAttachment)
        } catch (e: Exception) {
            Result.Error("Error al subir archivo: ${e.message}", e)
        }
    }

    override suspend fun downloadAttachment(attachment: Attachment): Result<String> {
        return try {
            // 1. Crear archivo temporal
            val tempFile = File.createTempFile(attachment.name, null)

            // 2. Descargar desde Firebase Storage
            val downloadedFile = storageService.downloadFile(
                path = attachment.url, // O extraer path del URL
                destinationFile = tempFile
            )

            Result.Success(downloadedFile.absolutePath)
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