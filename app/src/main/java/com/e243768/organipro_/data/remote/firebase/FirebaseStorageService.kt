package com.e243768.organipro_.data.remote.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.InputStream

class FirebaseStorageService(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth? = null
) {

    /**
     * Subir un archivo desde URI
     */
    suspend fun uploadFile(
        path: String,
        fileUri: Uri,
        metadata: StorageMetadata? = null,
        onProgress: ((Double) -> Unit)? = null
    ): String {
        try {
            val storageRef = storage.reference.child(path)

            // Diagnostics
            try {
                val bucket = storage.app.options.storageBucket ?: "(no-bucket)"
                val uid = auth?.currentUser?.uid ?: "(no-user)"
                println("[FirebaseStorageService] uploadFile -> bucket=$bucket path=${storageRef.path} uid=$uid")
            } catch (_: Exception) { }

            val uploadTask = if (metadata != null) {
                storageRef.putFile(fileUri, metadata)
            } else {
                storageRef.putFile(fileUri)
            }

            // Listener de progreso
            onProgress?.let { progressCallback ->
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    progressCallback(progress)
                }
            }

            uploadTask.await()

            // Obtener URL de descarga
            return storageRef.downloadUrl.await().toString()
        } catch (se: StorageException) {
            se.printStackTrace()
            val code = se.errorCode
            val msg = se.message ?: "StorageException"

            // Si es 404 intentar reintentar con bucket fallback
            if (msg.contains("Not Found", ignoreCase = true) || code == -13010) {
                try {
                    val projectId = try { com.google.firebase.FirebaseApp.getInstance().options.projectId } catch (_: Exception) { null }
                    if (!projectId.isNullOrBlank()) {
                        val fallbackBucket = "gs://$projectId.appspot.com"
                        println("[FirebaseStorageService] Detected 404; attempting upload with fallback bucket=$fallbackBucket")
                        val fallbackStorage = FirebaseStorage.getInstance(fallbackBucket)
                        val fallbackRef = fallbackStorage.reference.child(path)

                        val fallbackTask = if (metadata != null) {
                            fallbackRef.putFile(fileUri, metadata)
                        } else {
                            fallbackRef.putFile(fileUri)
                        }
                        fallbackTask.await()
                        return fallbackRef.downloadUrl.await().toString()
                    }
                } catch (fe: Exception) {
                    fe.printStackTrace()
                    // continuar y relanzar el error original
                }
            }

            throw Exception("StorageException(code=$code): $msg", se)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Subir un archivo desde bytes
     */
    suspend fun uploadBytes(
        path: String,
        bytes: ByteArray,
        metadata: StorageMetadata? = null
    ): String {
        return try {
            val storageRef = storage.reference.child(path)

            if (metadata != null) {
                storageRef.putBytes(bytes, metadata).await()
            } else {
                storageRef.putBytes(bytes).await()
            }

            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Descargar un archivo
     */
    suspend fun downloadFile(
        path: String,
        destinationFile: File
    ): File {
        return try {
            val storageRef = storage.reference.child(path)
            storageRef.getFile(destinationFile).await()
            destinationFile
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Obtener URL de descarga
     */
    suspend fun getDownloadUrl(path: String): String {
        return try {
            val storageRef = storage.reference.child(path)
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Eliminar un archivo
     */
    suspend fun deleteFile(path: String) {
        try {
            val storageRef = storage.reference.child(path)
            storageRef.delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Listar archivos en un directorio
     */
    suspend fun listFiles(path: String): List<StorageReference> {
        return try {
            val storageRef = storage.reference.child(path)
            val listResult = storageRef.listAll().await()
            listResult.items
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Obtener metadatos de un archivo
     */
    suspend fun getMetadata(path: String): StorageMetadata {
        return try {
            val storageRef = storage.reference.child(path)
            storageRef.metadata.await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Actualizar metadatos
     */
    suspend fun updateMetadata(path: String, metadata: StorageMetadata) {
        try {
            val storageRef = storage.reference.child(path)
            storageRef.updateMetadata(metadata).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Verificar si un archivo existe
     */
    suspend fun fileExists(path: String): Boolean {
        return try {
            val storageRef = storage.reference.child(path)
            storageRef.metadata.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtener referencia a un archivo
     */
    fun getFileReference(path: String): StorageReference {
        return storage.reference.child(path)
    }

    /**
     * Subir stream de un archivo (útil para content:// URIs grandes)
     */
    suspend fun uploadStream(
        path: String,
        stream: InputStream,
        metadata: StorageMetadata? = null,
        onProgress: ((Double) -> Unit)? = null
    ): String {
        try {
            val storageRef = storage.reference.child(path)

            try {
                val bucket = storage.app.options.storageBucket ?: "(no-bucket)"
                val uid = auth?.currentUser?.uid ?: "(no-user)"
                println("[FirebaseStorageService] uploadStream -> bucket=$bucket path=${storageRef.path} uid=$uid")
            } catch (_: Exception) { }

            val uploadTask = if (metadata != null) {
                storageRef.putStream(stream, metadata)
            } else {
                storageRef.putStream(stream)
            }

            onProgress?.let { progressCallback ->
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    progressCallback(progress)
                }
            }

            uploadTask.await()
            return storageRef.downloadUrl.await().toString()
        } catch (se: StorageException) {
            se.printStackTrace()
            val code = se.errorCode
            val msg = se.message ?: "StorageException"

            // Si es 404 intentar reintentar con bucket fallback
            if (msg.contains("Not Found", ignoreCase = true) || code == -13010) {
                try {
                    val projectId = try { com.google.firebase.FirebaseApp.getInstance().options.projectId } catch (_: Exception) { null }
                    if (!projectId.isNullOrBlank()) {
                        val fallbackBucket = "gs://$projectId.appspot.com"
                        println("[FirebaseStorageService] Detected 404; attempting uploadStream with fallback bucket=$fallbackBucket")
                        val fallbackStorage = FirebaseStorage.getInstance(fallbackBucket)
                        val fallbackRef = fallbackStorage.reference.child(path)

                        val fallbackTask = if (metadata != null) {
                            fallbackRef.putStream(stream, metadata)
                        } else {
                            fallbackRef.putStream(stream)
                        }
                        fallbackTask.await()
                        return fallbackRef.downloadUrl.await().toString()
                    }
                } catch (fe: Exception) {
                    fe.printStackTrace()
                    // continuar y relanzar el error original
                }
            }

            throw Exception("StorageException(code=$code): $msg", se)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Generar path único para attachment
     */
    fun generateAttachmentPath(userId: String, taskId: String, fileName: String): String {
        val timestamp = System.currentTimeMillis()
        return "attachments/$userId/$taskId/${timestamp}_$fileName"
    }

    /**
     * Generar path para avatar de usuario
     */
    fun generateAvatarPath(userId: String, fileName: String): String {
        return "avatars/$userId/$fileName"
    }
}