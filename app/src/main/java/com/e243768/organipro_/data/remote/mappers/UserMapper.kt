package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.User
import com.google.firebase.Timestamp
import java.util.Date

object UserMapper {

    /**
     * Convertir User domain a Map para Firebase
     */
    fun toFirebaseMap(user: User): Map<String, Any?> {
        return hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "alias" to user.alias,
            "email" to user.email,
            "avatarUrl" to user.avatarUrl,
            "level" to user.level,
            "currentXP" to user.currentXP,
            "totalPoints" to user.totalPoints,
            "currentStreak" to user.currentStreak,
            "longestStreak" to user.longestStreak,
            "tasksCompleted" to user.tasksCompleted,
            "createdAt" to Timestamp(user.createdAt),
            "updatedAt" to Timestamp(user.updatedAt),
            "lastLoginAt" to user.lastLoginAt?.let { Timestamp(it) },
            "isActive" to user.isActive
        )
    }

    /**
     * Convertir Map de Firebase a User domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): User {
        return User(
            id = map["id"] as? String ?: "",
            name = map["name"] as? String ?: "",
            alias = map["alias"] as? String ?: "",
            email = map["email"] as? String ?: "",
            avatarUrl = map["avatarUrl"] as? String,
            level = (map["level"] as? Long)?.toInt() ?: 1,
            currentXP = (map["currentXP"] as? Long)?.toInt() ?: 0,
            totalPoints = (map["totalPoints"] as? Long)?.toInt() ?: 0,
            currentStreak = (map["currentStreak"] as? Long)?.toInt() ?: 0,
            longestStreak = (map["longestStreak"] as? Long)?.toInt() ?: 0,
            tasksCompleted = (map["tasksCompleted"] as? Long)?.toInt() ?: 0,
            createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date(),
            updatedAt = (map["updatedAt"] as? Timestamp)?.toDate() ?: Date(),
            lastLoginAt = (map["lastLoginAt"] as? Timestamp)?.toDate(),
            isActive = map["isActive"] as? Boolean ?: true
        )
    }
}