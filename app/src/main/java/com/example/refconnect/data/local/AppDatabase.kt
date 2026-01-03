package com.example.refconnect.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.refconnect.data.local.dao.*
import com.example.refconnect.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        ReferralEntity::class,
        ScreeningTestEntity::class,
        QuestionEntity::class,
        TestAttemptEntity::class,
        ConnectionEntity::class,
        ChatEntity::class,
        MessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun referralDao(): ReferralDao
    abstract fun screeningTestDao(): ScreeningTestDao
    abstract fun questionDao(): QuestionDao
    abstract fun testAttemptDao(): TestAttemptDao
    abstract fun connectionDao(): ConnectionDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "refconnect_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }
}
