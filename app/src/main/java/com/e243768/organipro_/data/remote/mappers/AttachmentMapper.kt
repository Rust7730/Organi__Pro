package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.model.AttachmentType
import com.google.firebase.Timestamp
import java.util.Date

object AttachmentMapper {

    /**
     * Convertir Attachment domain a Map para Firebase
     */
    fun toFirebaseMap(attachment: Attachment): Map<String, Any?> {
        return hashMapOf(
            "id" to attachment.id,
            "taskId" to attachment.taskId,
            "name" to attachment.name,
            "type" to attachment.type.name,
            "mimeType" to attachment.mimeType,
            "size" to attachment.size,
            "url" to attachment.url,
            "localPath" to attachment.localPath,
            "uploadedAt" to Timestamp(attachment.uploadedAt),
            "isUploaded" to attachment.isUploaded
        )
    }

    /**
     * Convertir Map de Firebase a Attachment domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): Attachment {
        return Attachment(
            id = map["id"] as? String ?: "",
            taskId = map["taskId"] as? String ?: "",
            name = map["name"] as? String ?: "",
            type = AttachmentType.valueOf(map["type"] as? String ?: "OTHER"),
            mimeType = map["mimeType"] as? String ?: "",
            size = map["size"] as? Long ?: 0L,
            url = map["url"] as? String ?: "",
            localPath = map["localPath"] as? String,
            uploadedAt = (map["uploadedAt"] as? Timestamp)?.toDate() ?: Date(),
            isUploaded = map["isUploaded"] as? Boolean ?: false
        )
    }
}