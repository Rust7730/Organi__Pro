package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.*
import com.google.firebase.Timestamp
import java.util.Date

object TaskMapper {

    /**
     * Convertir Task domain a Map para Firebase
     */
    fun toFirebaseMap(task: Task): Map<String, Any?> {
        return hashMapOf(
            "id" to task.id,
            "userId" to task.userId,
            "title" to task.title,
            "description" to task.description,
            "priority" to task.priority.name,
            "status" to task.status.name,
            "points" to task.points,
            "dueDate" to task.dueDate?.let { Timestamp(it) },
            "scheduledTime" to task.scheduledTime,
            "estimatedDuration" to task.estimatedDuration,
            "progress" to task.progress,
            "tags" to task.tags,
            "createdAt" to Timestamp(task.createdAt),
            "updatedAt" to Timestamp(task.updatedAt),
            "completedAt" to task.completedAt?.let { Timestamp(it) },
            "isRecurring" to task.isRecurring,
            "recurringPattern" to task.recurringPattern?.let { pattern ->
                hashMapOf(
                    "type" to pattern.type.name,
                    "interval" to pattern.interval,
                    "daysOfWeek" to pattern.daysOfWeek,
                    "endDate" to pattern.endDate?.let { Timestamp(it) }
                )
            }
        )
    }

    /**
     * Convertir Map de Firebase a Task domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): Task {
        val recurringPatternMap = map["recurringPattern"] as? Map<String, Any?>
        val recurringPattern = recurringPatternMap?.let { patternMap ->
            RecurringPattern(
                type = RecurringType.valueOf(patternMap["type"] as? String ?: "DAILY"),
                interval = (patternMap["interval"] as? Long)?.toInt() ?: 1,
                daysOfWeek = (patternMap["daysOfWeek"] as? List<Long>)?.map { it.toInt() } ?: emptyList(),
                endDate = (patternMap["endDate"] as? Timestamp)?.toDate()
            )
        }

        return Task(
            id = map["id"] as? String ?: "",
            userId = map["userId"] as? String ?: "",
            title = map["title"] as? String ?: "",
            description = map["description"] as? String ?: "",
            priority = Priority.valueOf(map["priority"] as? String ?: "MEDIA"),
            status = TaskStatus.valueOf(map["status"] as? String ?: "PENDING"),
            points = (map["points"] as? Long)?.toInt() ?: 0,
            dueDate = (map["dueDate"] as? Timestamp)?.toDate(),
            scheduledTime = map["scheduledTime"] as? String,
            estimatedDuration = (map["estimatedDuration"] as? Long)?.toInt() ?: 30,
            progress = (map["progress"] as? Double)?.toFloat() ?: 0f,
            attachments = emptyList(), // Se cargan por separado
            tags = (map["tags"] as? List<String>) ?: emptyList(),
            createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date(),
            updatedAt = (map["updatedAt"] as? Timestamp)?.toDate() ?: Date(),
            completedAt = (map["completedAt"] as? Timestamp)?.toDate(),
            isRecurring = map["isRecurring"] as? Boolean ?: false,
            recurringPattern = recurringPattern
        )
    }
}