package com.example.util

import com.example.data.MemoryEntity

enum class SmartCategory(
    val titleZh: String,
    val titleEn: String,
    val icon: String,
    val descriptionZh: String,
    val descriptionEn: String
) {
    FAMILY_GATHERING(
        titleZh = "家庭聚会",
        titleEn = "Family Gatherings",
        icon = "👨‍👩‍👧‍👦",
        descriptionZh = "欢聚一堂，四代同堂的温馨聚会照片",
        descriptionEn = "Heartwarming moments of family reunions and gatherings"
    ),
    VACATION(
        titleZh = "旅行美景",
        titleEn = "Vacations & Travel",
        icon = "🏖️",
        descriptionZh = "名山大川、京城风景与海滨度假记忆",
        descriptionEn = "Scenic travel, vacation tours, and adventure memories"
    ),
    HOLIDAY(
        titleZh = "节日庆典",
        titleEn = "Holidays & Celebrations",
        icon = "🧧",
        descriptionZh = "春节喜庆、中秋佳节与新年家庭团圆",
        descriptionEn = "Spring Festival, holidays, and festive family dinners"
    ),
    MILESTONE(
        titleZh = "人生里程碑",
        titleEn = "Milestones & Anniversaries",
        icon = "🏆",
        descriptionZh = "金婚纪念、生日晚宴与人生珍贵时刻",
        descriptionEn = "Golden anniversaries, birthdays, and life milestones"
    )
}

data class CategorizedMemory(
    val memory: MemoryEntity,
    val category: SmartCategory,
    val confidenceScore: Int, // e.g. 96 -> 96% confidence
    val matchingKeyword: String
)

object SmartPhotoCategorizer {

    fun categorizeMemory(memory: MemoryEntity): CategorizedMemory {
        val combinedText = "${memory.title} ${memory.titleEn} ${memory.description} ${memory.descriptionEn} ${memory.location} ${memory.locationEn} ${memory.tags} ${memory.eventName}".lowercase()

        // 1. Check Holiday keywords
        val holidayKeywords = listOf("春节", "中秋", "国庆", "新年", "元旦", "节日", "holiday", "festival", "christmas", "new year")
        for (kw in holidayKeywords) {
            if (combinedText.contains(kw)) {
                return CategorizedMemory(memory, SmartCategory.HOLIDAY, 98, kw)
            }
        }

        // 2. Check Milestone keywords
        val milestoneKeywords = listOf("金婚", "银婚", "生日", "婚礼", "周年", "50", "wedding", "anniversary", "birthday", "milestone")
        for (kw in milestoneKeywords) {
            if (combinedText.contains(kw)) {
                return CategorizedMemory(memory, SmartCategory.MILESTONE, 96, kw)
            }
        }

        // 3. Check Vacation keywords
        val vacationKeywords = listOf("北京", "海边", "三亚", "黄山", "旅游", "风景", "游玩", "beach", "trip", "vacation", "tour", "mountain")
        for (kw in vacationKeywords) {
            if (combinedText.contains(kw)) {
                return CategorizedMemory(memory, SmartCategory.VACATION, 94, kw)
            }
        }

        // 4. Default to Family Gathering
        return CategorizedMemory(memory, SmartCategory.FAMILY_GATHERING, 92, "family")
    }

    fun groupMemoriesBySmartCategory(memories: List<MemoryEntity>): Map<SmartCategory, List<CategorizedMemory>> {
        return memories.map { categorizeMemory(it) }
            .groupBy { it.category }
    }
}
