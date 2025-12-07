package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.UserRank

object UserRankMapper {

    /**
     * Convertir UserRank domain a Map para Firebase
     */
    fun toFirebaseMap(userRank: UserRank): Map<String, Any?> {
        return hashMapOf(
            "userId" to userRank.userId,
            "userName" to userRank.userName,
            "userAlias" to userRank.userAlias,
            "avatarUrl" to userRank.avatarUrl,
            "rank" to userRank.rank,
            "points" to userRank.points,
            "weeklyPoints" to userRank.weeklyPoints,
            "monthlyPoints" to userRank.monthlyPoints,
            "level" to userRank.level,
            "streak" to userRank.streak,
            "isCurrentUser" to userRank.isCurrentUser
        )
    }

    /**
     * Convertir Map de Firebase a UserRank domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): UserRank {
        return UserRank(
            userId = map["userId"] as? String ?: "",
            userName = map["userName"] as? String ?: "",
            userAlias = map["userAlias"] as? String ?: "",
            avatarUrl = map["avatarUrl"] as? String,
            rank = (map["rank"] as? Long)?.toInt() ?: 0,
            points = (map["points"] as? Long)?.toInt() ?: 0,
            weeklyPoints = (map["weeklyPoints"] as? Long)?.toInt() ?: 0,
            monthlyPoints = (map["monthlyPoints"] as? Long)?.toInt() ?: 0,
            level = (map["level"] as? Long)?.toInt() ?: 1,
            streak = (map["streak"] as? Long)?.toInt() ?: 0,
            isCurrentUser = map["isCurrentUser"] as? Boolean ?: false
        )
    }
}