package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val englishName: String,
    val role: String,
    val roleEn: String,
    val isElder: Boolean,
    val postsCount: Int,
    val avatarResName: String,
    val activeTime: String,
    val activeTimeEn: String,
    val generation: Int // 1, 2, 3
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleEn: String,
    val description: String,
    val descriptionEn: String,
    val location: String,
    val locationEn: String,
    val date: String,
    val cameraModel: String,
    val photoResNames: String, // comma separated list
    val likesCount: Int,
    val commentsCount: Int,
    val rating: Float,
    val isEncrypted: Boolean,
    val tags: String, // comma separated
    val eventName: String = "",
    val eventNameEn: String = "",
    val isHistoricalHighlight: Boolean = false,
    val isOfflineCached: Boolean = true
)

@Entity(tableName = "story_chapters")
data class StoryChapterEntity(
    @PrimaryKey val id: String,
    val chapterCode: String,
    val year: String,
    val title: String,
    val titleEn: String,
    val description: String,
    val descriptionEn: String,
    val voiceClipsCount: Int,
    val photoCount: Int
)

@Entity(tableName = "app_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val senderName: String,
    val actionText: String,
    val actionTextEn: String,
    val detailText: String,
    val timeAgo: String,
    val type: String, // "like", "comment", "mention", "system"
    val avatarResName: String
)

data class CommentModel(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val content: String,
    val timeAgo: String,
    val avatarResName: String,
    val rating: Float = 5.0f
)

data class VideoTemplateModel(
    val id: String,
    val title: String,
    val titleEn: String,
    val duration: String,
    val category: String,
    val thumbnailResName: String,
    val videoUrl: String = ""
)
