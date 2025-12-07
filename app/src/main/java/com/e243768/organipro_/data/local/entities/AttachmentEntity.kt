package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.model.AttachmentType
import java.util.Date

@Entity(
    tableName = DatabaseConstants.TABLE_ATTACHMENTS,
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = [DatabaseConstants.COLUMN_ID],
            childColumns = [DatabaseConstants.COLUMN_ATTACHMENT_TASK_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [DatabaseConstants.COLUMN_ATTACHMENT_TASK_ID])]
)
data class AttachmentEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_ATTACHMENT_TASK_ID)
    val taskId: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_ATTACHMENT_NAME)
    val name: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_ATTACHMENT_TYPE)
    val type: AttachmentType,

    @ColumnInfo(name = "mime_type")
    val mimeType: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_ATTACHMENT_SIZE)
    val size: Long,

    @ColumnInfo(name = DatabaseConstants.COLUMN_ATTACHMENT_URL)
    val url: String,

    @ColumnInfo(name = "local_path")
    val localPath: String?,

    @ColumnInfo(name = "uploaded_at")
    val uploadedAt: Date,

    @ColumnInfo(name = "is_uploaded")
    val isUploaded: Boolean,

    @ColumnInfo(name = DatabaseConstants.COLUMN_SYNCED)
    val synced: Boolean = false
) {
    fun toDomain(): Attachment {
        return Attachment(
            id = id,
            taskId = taskId,
            name = name,
            type = type,
            mimeType = mimeType,
            size = size,
            url = url,
            localPath = localPath,
            uploadedAt = uploadedAt,
            isUploaded = isUploaded
        )
    }

    companion object {
        fun fromDomain(attachment: Attachment, synced: Boolean = false): AttachmentEntity {
            return AttachmentEntity(
                id = attachment.id,
                taskId = attachment.taskId,
                name = attachment.name,
                type = attachment.type,
                mimeType = attachment.mimeType,
                size = attachment.size,
                url = attachment.url,
                localPath = attachment.localPath,
                uploadedAt = attachment.uploadedAt,
                isUploaded = attachment.isUploaded,
                synced = synced
            )
        }
    }
}