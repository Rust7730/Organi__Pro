package com.e243768.organipro_.domain.model

import java.util.Date
import java.util.UUID

data class Attachment(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String = "",
    val name: String = "",
    val type: AttachmentType = AttachmentType.OTHER,
    val mimeType: String = "",
    val size: Long = 0, // En bytes
    val url: String = "",
    val localPath: String? = null,
    val uploadedAt: Date = Date(),
    val isUploaded: Boolean = false
) {
    /**
     * Obtiene el tamaño formateado
     */
    fun getFormattedSize(): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0

        return when {
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }

    /**
     * Obtiene la extensión del archivo
     */
    fun getExtension(): String {
        return name.substringAfterLast('.', "")
    }

    /**
     * Verifica si es una imagen
     */
    fun isImage(): Boolean = type == AttachmentType.IMAGE

    /**
     * Verifica si es un PDF
     */
    fun isPdf(): Boolean = type == AttachmentType.PDF
}

enum class AttachmentType {
    IMAGE,
    PDF,
    DOCUMENT,
    VIDEO,
    AUDIO,
    OTHER;

    companion object {
        fun fromMimeType(mimeType: String): AttachmentType {
            return when {
                mimeType.startsWith("image/") -> IMAGE
                mimeType == "application/pdf" -> PDF
                mimeType.startsWith("application/") -> DOCUMENT
                mimeType.startsWith("video/") -> VIDEO
                mimeType.startsWith("audio/") -> AUDIO
                else -> OTHER
            }
        }

        fun fromExtension(extension: String): AttachmentType {
            return when (extension.lowercase()) {
                "jpg", "jpeg", "png", "gif", "webp" -> IMAGE
                "pdf" -> PDF
                "doc", "docx", "txt", "xls", "xlsx" -> DOCUMENT
                "mp4", "avi", "mov" -> VIDEO
                "mp3", "wav", "m4a" -> AUDIO
                else -> OTHER
            }
        }
    }
}