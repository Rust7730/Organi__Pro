package com.e243768.organipro_.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseStorageService(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
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
        return try {
            val storageRef = storage.reference.child(path)

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
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
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