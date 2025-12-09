package com.e243768.organipro_.data.repository

import android.net.Uri
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.AttachmentDao
import com.e243768.organipro_.data.local.entities.AttachmentEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.firebase.FirebaseStorageService
import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.model.AttachmentType
import com.e243768.organipro_.domain.repository.AttachmentRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val firestoreService: FirebaseFirestoreService,
    private val storageService: FirebaseStorageService,
    @ApplicationContext private val context: Context
) : AttachmentRepository {

    override suspend fun addAttachment(attachment: Attachment): Result<Attachment> {
        return try {
            // Generar id si es necesario
            val id = if (attachment.id.isBlank()) UUID.randomUUID().toString() else attachment.id
            val toSave = attachment.copy(id = id)

            // Guardar en local primero (optimista)
            val entity = AttachmentEntity.fromDomain(toSave)
            attachmentDao.insertAttachment(entity)

            // Si hay una ruta local, intentar subir inmediatamente (puede fallar y dejarlo como pendiente)
            val localPath = toSave.localPath
            if (!localPath.isNullOrBlank()) {
                try {
                    val uploadResult = uploadAttachment(toSave, localPath)
                    if (uploadResult is Result.Success) {
                        return uploadResult
                    } else {
                        // Si la subida falló, devolvemos el adjunto guardado localmente para que la UI lo muestre como pendiente
                        return Result.Success(toSave)
                    }
                } catch (_: Exception) {
                    // Ignorar error y devolver el guardado local
                    return Result.Success(toSave)
                }
            }

            // Si no había ruta local, devolvemos el guardado
            Result.Success(toSave)
        } catch (_: Exception) {
            Result.Error("Error al guardar adjunto")
        }
    }

    override suspend fun getAttachmentById(attachmentId: String): Result<Attachment> {
        return try {
            val entity = attachmentDao.getAttachmentById(attachmentId)
            if (entity != null) Result.Success(entity.toDomain()) else Result.Error("Adjunto no encontrado")
        } catch (_: Exception) {
            Result.Error("Error al obtener adjunto")
        }
    }

    override fun getAttachmentsByTaskIdFlow(taskId: String): Flow<List<Attachment>> {
        return attachmentDao.getAttachmentsByTaskIdFlow(taskId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAttachmentsByTaskId(taskId: String): Result<List<Attachment>> {
        return try {
            // Intentar sincronizar remota -> local (no crítico)
            syncAttachmentsForTask(taskId)

            val entities = attachmentDao.getAttachmentsByTaskId(taskId)
            Result.Success(entities.map { it.toDomain() })
        } catch (_: Exception) {
            // Si falla la sync, devolver lo local si es posible
            try {
                val entities = attachmentDao.getAttachmentsByTaskId(taskId)
                Result.Success(entities.map { it.toDomain() })
            } catch (_: Exception) {
                Result.Error("Error al obtener adjuntos")
            }
        }
    }

    override suspend fun deleteAttachment(attachmentId: String): Result<Unit> {
        return try {
            attachmentDao.deleteAttachmentById(attachmentId)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error("Error al eliminar adjunto")
        }
    }

    override suspend fun deleteAttachmentsByTaskId(taskId: String): Result<Unit> {
        return try {
            attachmentDao.deleteAttachmentsByTaskId(taskId)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error("Error al eliminar adjuntos por tarea")
        }
    }

    override suspend fun uploadAttachment(attachment: Attachment, localFilePath: String): Result<Attachment> {
        return try {
            if (localFilePath.isBlank()) return Result.Error("Ruta local vacía")

            val uri = Uri.parse(localFilePath)
            // Sanear el nombre para evitar caracteres problemáticos (ej. ':', '/', etc.)
            val originalName = attachment.name
            val safeName = originalName.replace(Regex("[^A-Za-z0-9_.-]"), "_")
            val path = "${FirebaseConstants.STORAGE_ATTACHMENTS}/${attachment.taskId}/${System.currentTimeMillis()}_${safeName}"
            println("[AttachmentRepo] originalName=$originalName safeName=$safeName path=$path")

            // Intentar calcular tamaño local como fallback
            var localSize = 0L
            // Si es file:// o path absoluto, comprobar file
            try {
                val maybeFile = when {
                    uri.scheme == "file" -> File(uri.path ?: "")
                    File(localFilePath).exists() -> File(localFilePath)
                    else -> null
                }
                if (maybeFile != null && maybeFile.exists()) localSize = maybeFile.length()
            } catch (_: Exception) { localSize = 0L }

            // Preparar metadata con mimeType si está disponible
            val metadata = try {
                if (attachment.mimeType.isNotBlank()) com.google.firebase.storage.StorageMetadata.Builder()
                    .setContentType(attachment.mimeType)
                    .build() else null
            } catch (_: Exception) { null }

            // Si es content://, preferimos subir por stream para evitar OOM y problemas de permisos
            val downloadUrl = try {
                if (uri.scheme == "content") {
                    val inputStream = try {
                        context.contentResolver.openInputStream(uri)
                    } catch (se: SecurityException) {
                        // Falta permiso para leer el URI
                        return Result.Error("Sin permiso para leer el archivo (SecurityException). Asegúrate de usar OpenDocument y persistir permiso.", se)
                    } catch (e: Exception) {
                        return Result.Error("Error abriendo InputStream: ${e.message}", e)
                    }

                    if (inputStream != null) {
                        try {
                            // No leemos todo en memoria, usamos putStream
                            val url = storageService.uploadStream(path, inputStream, metadata)

                            // Intentar estimar tamaño si es posible (ContentResolver puede devolver tamaño mediante openAssetFileDescriptor)
                            try {
                                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                                    if (afd.length > 0) localSize = afd.length
                                }
                            } catch (e: Exception) {
                                // Ignorar error al estimar tamaño
                            }

                            url
                        } catch (e: Exception) {
                            // Detectar errores comunes de Firebase Storage
                            return Result.Error("Error al subir el stream: ${e.message}", e)
                        } finally {
                            try { inputStream.close() } catch (_: Exception) { }
                        }
                    } else {
                        // Fallback a putFile si no pudimos abrir stream
                        try {
                            storageService.uploadFile(path, uri, metadata)
                        } catch (e: Exception) {
                            return Result.Error("Error al subir archivo por fallback (putFile): ${e.message}", e)
                        }
                    }
                } else {
                    // file:// o contentless path -> usar putFile
                    storageService.uploadFile(path, uri, metadata)
                }
            } catch (e: Exception) {
                return Result.Error("Error al subir archivo: ${e::class.simpleName}: ${e.message}", e)
            }

            // Log para debugging: mostrar la URL y tamaño calculado
            println("[AttachmentRepo] Uploaded to path=$path, remoteUrl=$downloadUrl, estimatedLocalSize=$localSize")

            // Obtener metadatos (tamaño, etc.) desde Storage con reintentos (manejar latencia/propagación)
            var remoteMetadata = try { storageService.getMetadata(path) } catch (_: Exception) { null }
            if (remoteMetadata == null || remoteMetadata.sizeBytes <= 0L) {
                var attempts = 0
                while (attempts < 3 && (remoteMetadata == null || remoteMetadata.sizeBytes <= 0L)) {
                    attempts++
                    delay(500)
                    remoteMetadata = try { storageService.getMetadata(path) } catch (_: Exception) { null }
                }
            }

            // Preferir metadatos remotos, si no, usar tamaño local calculado, si no, mantener el tamaño previo
            val size = when {
                remoteMetadata != null && remoteMetadata.sizeBytes > 0L -> remoteMetadata.sizeBytes
                localSize > 0L -> localSize
                else -> attachment.size
            }

            val now = Date()

            val updated = attachment.copy(
                url = downloadUrl,
                isUploaded = true,
                localPath = null,
                size = size,
                uploadedAt = now
            )

            // LOG: mostrar info útil para debugging
            println("Attachment uploaded: path=$path url=$downloadUrl size=$size id=${updated.id}")

            // Actualizar local con metadatos reales
            attachmentDao.insertAttachment(AttachmentEntity.fromDomain(updated, synced = true))

            // Guardar metadatos en Firestore (map para evitar problemas de serialización)
            val mapData = mapOf(
                "id" to updated.id,
                "taskId" to updated.taskId,
                "name" to updated.name,
                "mimeType" to updated.mimeType,
                "type" to updated.type.name,
                "url" to updated.url,
                "size" to updated.size,
                "uploadedAt" to updated.uploadedAt,
                "isUploaded" to updated.isUploaded
            )

            try {
                firestoreService.setDocument(FirebaseConstants.COLLECTION_ATTACHMENTS, updated.id, mapData, merge = true)
                println("Attachment metadata saved to Firestore: id=${updated.id}")
            } catch (e: Exception) {
                // Si no se pudo guardar metadatos, registrar el error pero no fallar la operación principal
                println("[AttachmentRepo] Error guardando metadata en Firestore: ${e::class.simpleName}: ${e.message}")
            }

            Result.Success(updated)
        } catch (e: Exception) {
            // Registrar traza completa para diagnostico
            e.printStackTrace()

            // Si el mensaje indica Not Found/404, es muy probable que el bucket esté mal configurado
            val lower = (e.message ?: "").lowercase()
            if (lower.contains("not found") || lower.contains("404") || lower.contains("object does not exist")) {
                val hint = "No se encontró el bucket de Firebase Storage o la ruta es inválida. " +
                        "Verifica que 'storage_bucket' en google-services.json coincida con el bucket del proyecto (p.ej. gs://<project-id>.appspot.com), " +
                        "descarga el google-services.json correcto desde Firebase Console y recompila la app. También revisa las reglas de Storage y App Check."
                println("[AttachmentRepo] ERROR 404 upload: $hint")
                return Result.Error(hint, e)
            }

            Result.Error("Error en uploadAttachment: ${e::class.simpleName}: ${e.message}", e)
        }
    }

    override suspend fun downloadAttachment(attachment: Attachment): Result<String> {
        return try {
            // Si ya tenemos URL retornarla; la descarga física puede ser manejada por otra capa
            val url = attachment.url
            if (url.isBlank()) return Result.Error("No hay URL para descargar")
            Result.Success(url)
        } catch (_: Exception) {
            Result.Error("Error al descargar adjunto")
        }
    }

    override suspend fun getPendingUploads(): Result<List<Attachment>> {
        return try {
            val entities = attachmentDao.getPendingUploads()
            Result.Success(entities.map { it.toDomain() })
        } catch (_: Exception) {
            Result.Error("Error al obtener pendientes")
        }
    }

    override suspend fun syncPendingUploads(): Result<Unit> {
        return try {
            val pending = attachmentDao.getPendingUploads()
            pending.forEach { entity ->
                val attachment = entity.toDomain()
                val localPath = attachment.localPath
                if (!localPath.isNullOrBlank()) {
                    try {
                        val res = uploadAttachment(attachment, localPath)
                        if (res is Result.Error) {
                            // registrar y continuar con los demás
                            println("Error subiendo pendiente: ${res.getErrorMessage()}")
                        }
                    } catch (_: Exception) {
                        // ignorar y continuar
                    }
                }
            }
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error("Error al sincronizar pendientes")
        }
    }

    override suspend fun updateUploadStatus(attachmentId: String, isUploaded: Boolean, url: String): Result<Unit> {
        return try {
            attachmentDao.updateUploadStatus(attachmentId, isUploaded, url)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error("Error al actualizar estado de subida")
        }
    }

    // --- Función interna de sincronización por tarea ---
    private suspend fun syncAttachmentsForTask(taskId: String) {
        try {
            val remoteDocs = firestoreService.getDocuments(
                collection = FirebaseConstants.COLLECTION_ATTACHMENTS,
                clazz = Map::class.java
            ) { query ->
                query.whereEqualTo("taskId", taskId)
            }

            if (remoteDocs.isNotEmpty()) {
                val remoteAttachments = remoteDocs.mapNotNull { docAny ->
                    val doc = docAny as Map<*, *>
                    try {
                        val id = doc["id"] as? String ?: return@mapNotNull null
                        val name = doc["name"] as? String ?: "Archivo"
                        val mimeType = doc["mimeType"] as? String ?: ""

                        val typeStr = doc["type"] as? String ?: "OTHER"
                        val type = try {
                            AttachmentType.valueOf(typeStr)
                        } catch (_: Exception) {
                            AttachmentType.OTHER
                        }

                        val url = doc["url"] as? String ?: ""

                        val size = when (val s = doc["size"]) {
                            is Number -> s.toLong()
                            is String -> s.toLongOrNull() ?: 0L
                            else -> 0L
                        }

                        val uploadedAt = when (val u = doc["uploadedAt"]) {
                            is Timestamp -> u.toDate()
                            is Date -> u
                            is Number -> Date(u.toLong())
                            is String -> {
                                val asLong = u.toLongOrNull()
                                if (asLong != null) Date(asLong) else Date()
                            }
                            else -> Date()
                        }

                        Attachment(
                            id = id,
                            taskId = taskId,
                            name = name,
                            type = type,
                            mimeType = mimeType,
                            size = size,
                            url = url,
                            uploadedAt = uploadedAt,
                            isUploaded = (doc["isUploaded"] as? Boolean) ?: false
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                val entities = remoteAttachments.map { AttachmentEntity.fromDomain(it, synced = true) }
                if (entities.isNotEmpty()) {
                    attachmentDao.insertAttachments(entities)
                }
            }
        } catch (_: Exception) {
            println("Error sincronizando adjuntos")
        }
    }
}