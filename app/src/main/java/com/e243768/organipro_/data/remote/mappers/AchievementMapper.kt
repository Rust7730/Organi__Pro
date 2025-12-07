package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.Achievement
import com.e243768.organipro_.domain.model.AchievementType
import com.google.firebase.Timestamp
import java.util.Date

object AchievementMapper {

    /**
     * Convertir Achievement domain a Map para Firebase
     */
    fun toFirebaseMap(achievement: Achievement): Map<String, Any?> {
        return hashMapOf(
            "id" to achievement.id,
            "userId" to achievement.userId,
            "type" to achievement.type.name,
            "name" to achievement.name,
            "description" to achievement.description,
            "iconUrl" to achievement.iconUrl,
            "progress" to achievement.progress,
            "target" to achievement.target,
            "isUnlocked" to achievement.isUnlocked,
            "unlockedAt" to achievement.unlockedAt?.let { Timestamp(it) },
            "rewardPoints" to achievement.rewardPoints
        )
    }

    /**
     * Convertir Map de Firebase a Achievement domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): Achievement {
        return Achievement(
            id = map["id"] as? String ?: "",
            userId = map["userId"] as? String ?: "",
            type = AchievementType.valueOf(map["type"] as? String ?: "TASKS_COMPLETED"),
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            iconUrl = map["iconUrl"] as? String,
            progress = (map["progress"] as? Long)?.toInt() ?: 0,
            target = (map["target"] as? Long)?.toInt() ?: 0,
            isUnlocked = map["isUnlocked"] as? Boolean ?: false,
            unlockedAt = (map["unlockedAt"] as? Timestamp)?.toDate(),
            rewardPoints = (map["rewardPoints"] as? Long)?.toInt() ?: 0
        )
    }
}