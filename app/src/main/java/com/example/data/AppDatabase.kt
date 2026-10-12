package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_members ORDER BY generation ASC, isElder DESC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<FamilyMemberEntity>)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY date DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY date DESC")
    suspend fun getAllMemoriesOnce(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE isEncrypted = 1 ORDER BY date DESC")
    fun getEncryptedMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isHistoricalHighlight = 1 ORDER BY date ASC")
    fun getHistoricalHighlights(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isOfflineCached = 1 ORDER BY date DESC")
    fun getOfflineCachedMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>)

    @Query("DELETE FROM memories WHERE id IN (:ids)")
    suspend fun deleteMemories(ids: List<String>)

    @Query("UPDATE memories SET tags = tags || ', ' || :albumTag, eventName = :albumTag WHERE id IN (:ids)")
    suspend fun moveMemoriesToAlbum(ids: List<String>, albumTag: String)
}

@Dao
interface StoryChapterDao {
    @Query("SELECT * FROM story_chapters ORDER BY year ASC")
    fun getAllChapters(): Flow<List<StoryChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<StoryChapterEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
}

@Database(
    entities = [
        FamilyMemberEntity::class,
        MemoryEntity::class,
        StoryChapterEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun memoryDao(): MemoryDao
    abstract fun storyChapterDao(): StoryChapterDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "golden_memory_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
