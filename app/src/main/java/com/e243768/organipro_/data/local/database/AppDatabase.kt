package com.e243768.organipro_.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.e243768.organipro_.data.local.dao.*
import com.e243768.organipro_.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        AttachmentEntity::class,
        UserStatsEntity::class,
        AchievementEntity::class,
        UserRankEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userRankDao(): UserRankDao

    companion object {
        const val DATABASE_NAME = "organipro_database"
    }
}