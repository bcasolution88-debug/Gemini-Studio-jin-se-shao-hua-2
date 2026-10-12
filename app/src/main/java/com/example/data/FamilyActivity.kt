package com.example.data

enum class ActivityType {
    NEW_PHOTO,
    NEW_COMMENT,
    LIKE_PHOTO,
    AI_CAPTION
}

data class FamilyActivityItem(
    val id: String,
    val userName: String,
    val userNameEn: String,
    val avatarResId: Int,
    val activityType: ActivityType,
    val targetMemoryTitle: String,
    val targetMemoryTitleEn: String,
    val content: String,
    val contentEn: String,
    val timestamp: String,
    var likesCount: Int,
    var commentsList: List<String> = emptyList()
)
