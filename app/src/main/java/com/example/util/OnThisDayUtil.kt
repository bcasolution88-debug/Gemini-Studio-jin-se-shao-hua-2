package com.example.util

import com.example.data.MemoryEntity

data class OnThisDayItem(
    val memory: MemoryEntity,
    val yearsAgo: Int,
    val formattedDate: String,
    val exifSummary: String,
    val nostalgiaQuoteZh: String,
    val nostalgiaQuoteEn: String
)

object OnThisDayUtil {

    private const val CURRENT_YEAR = 2026

    /**
     * Parses memory metadata to filter and calculate 'On This Day' memories.
     * Extracts date YYYY-MM-DD, checks month-day matching (default 07-26),
     * calculates years elapsed, and formats EXIF camera metadata.
     */
    fun getOnThisDayMemories(
        memories: List<MemoryEntity>,
        targetMonthDay: String = "07-26"
    ): List<OnThisDayItem> {
        val result = mutableListOf<OnThisDayItem>()

        memories.forEach { mem ->
            val dateParts = mem.date.split("-")
            if (dateParts.size == 3) {
                val year = dateParts[0].toIntOrNull() ?: CURRENT_YEAR
                val monthDay = "${dateParts[1]}-${dateParts[2]}"

                if (monthDay == targetMonthDay && year < CURRENT_YEAR) {
                    val yearsAgo = CURRENT_YEAR - year
                    val formattedDate = "${dateParts[0]}年${dateParts[1]}月${dateParts[2]}日 (${yearsAgo}年前的今天)"

                    val quoteZh = when {
                        yearsAgo >= 30 -> "30载如白驹过隙，当年的欢笑与温情在老照片里熠熠生辉。"
                        yearsAgo >= 10 -> "十一载春华秋实，长辈与晚辈的深情厚谊弥足珍贵。"
                        else -> "几年前的今天，全家人的幸福定格在此刻。"
                    }

                    val quoteEn = when {
                        yearsAgo >= 30 -> "30 years flown by, yet family warmth shines brightly in this photo."
                        yearsAgo >= 10 -> "Over a decade of treasured moments and deep family bond."
                        else -> "A few years ago today, family happiness frozen in time."
                    }

                    val exif = if (mem.cameraModel.isNotEmpty()) {
                        "📷 EXIF: ${mem.cameraModel} · ${mem.location}"
                    } else {
                        "📷 EXIF: Phone Camera · ${mem.location}"
                    }

                    result.add(
                        OnThisDayItem(
                            memory = mem,
                            yearsAgo = yearsAgo,
                            formattedDate = formattedDate,
                            exifSummary = exif,
                            nostalgiaQuoteZh = quoteZh,
                            nostalgiaQuoteEn = quoteEn
                        )
                    )
                }
            }
        }

        // Return sorted by most recent years ago first
        return result.sortedBy { it.yearsAgo }
    }
}
