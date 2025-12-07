package com.e243768.organipro_.data.remote.mappers

import com.e243768.organipro_.domain.model.UserStats

object UserStatsMapper {

    /**
     * Convertir UserStats domain a Map para Firebase
     */
    fun toFirebaseMap(stats: UserStats): Map<String, Any?> {
        return hashMapOf(
            "userId" to stats.userId,
            "totalPoints" to stats.totalPoints,
            "weeklyPoints" to stats.weeklyPoints,
            "monthlyPoints" to stats.monthlyPoints,
            "tasksCompleted" to stats.tasksCompleted,
            "tasksCompletedToday" to stats.tasksCompletedToday,
            "tasksCompletedThisWeek" to stats.tasksCompletedThisWeek,
            "tasksCompletedThisMonth" to stats.tasksCompletedThisMonth,
            "currentStreak" to stats.currentStreak,
            "longestStreak" to stats.longestStreak,
            "averageTasksPerDay" to stats.averageTasksPerDay,
            "completionRate" to stats.completionRate,
            "totalTasksCreated" to stats.totalTasksCreated,
            "highPriorityCompleted" to stats.highPriorityCompleted,
            "mediumPriorityCompleted" to stats.mediumPriorityCompleted,
            "lowPriorityCompleted" to stats.lowPriorityCompleted
        )
    }

    /**
     * Convertir Map de Firebase a UserStats domain
     */
    fun fromFirebaseMap(map: Map<String, Any?>): UserStats {
        return UserStats(
            userId = map["userId"] as? String ?: "",
            totalPoints = (map["totalPoints"] as? Long)?.toInt() ?: 0,
            weeklyPoints = (map["weeklyPoints"] as? Long)?.toInt() ?: 0,
            monthlyPoints = (map["monthlyPoints"] as? Long)?.toInt() ?: 0,
            tasksCompleted = (map["tasksCompleted"] as? Long)?.toInt() ?: 0,
            tasksCompletedToday = (map["tasksCompletedToday"] as? Long)?.toInt() ?: 0,
            tasksCompletedThisWeek = (map["tasksCompletedThisWeek"] as? Long)?.toInt() ?: 0,
            tasksCompletedThisMonth = (map["tasksCompletedThisMonth"] as? Long)?.toInt() ?: 0,
            currentStreak = (map["currentStreak"] as? Long)?.toInt() ?: 0,
            longestStreak = (map["longestStreak"] as? Long)?.toInt() ?: 0,
            averageTasksPerDay = (map["averageTasksPerDay"] as? Double)?.toFloat() ?: 0f,
            completionRate = (map["completionRate"] as? Double)?.toFloat() ?: 0f,
            totalTasksCreated = (map["totalTasksCreated"] as? Long)?.toInt() ?: 0,
            highPriorityCompleted = (map["highPriorityCompleted"] as? Long)?.toInt() ?: 0,
            mediumPriorityCompleted = (map["mediumPriorityCompleted"] as? Long)?.toInt() ?: 0,
            lowPriorityCompleted = (map["lowPriorityCompleted"] as? Long)?.toInt() ?: 0
        )
    }
}