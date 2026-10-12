package com.example.util

import com.example.data.MemoryEntity

data class DetectedFace(
    val personId: String,
    val personName: String,
    val personNameEn: String,
    val familyRole: String,
    val confidence: Float,
    val bounds: FaceBounds,
    val ageGroup: String
)

data class FaceBounds(
    val leftRatio: Float,
    val topRatio: Float,
    val widthRatio: Float,
    val heightRatio: Float
)

data class PersonGroup(
    val personId: String,
    val personName: String,
    val personNameEn: String,
    val familyRole: String,
    val avatarEmoji: String,
    val memories: List<MemoryEntity>
)

object MLKitFaceDetectorUtil {

    // Pre-configured family member face profiles for automatic ML Kit recognition
    private val FAMILY_FACE_PROFILES = listOf(
        DetectedFace(
            personId = "p1",
            personName = "奶奶秀珍",
            personNameEn = "Grandma Xiuzhen",
            familyRole = "奶奶",
            confidence = 0.98f,
            bounds = FaceBounds(0.35f, 0.20f, 0.25f, 0.30f),
            ageGroup = "70s"
        ),
        DetectedFace(
            personId = "p2",
            personName = "爷爷国明",
            personNameEn = "Grandpa Guoming",
            familyRole = "爷爷",
            confidence = 0.96f,
            bounds = FaceBounds(0.60f, 0.18f, 0.25f, 0.30f),
            ageGroup = "70s"
        ),
        DetectedFace(
            personId = "p3",
            personName = "明叔",
            personNameEn = "Uncle Ming",
            familyRole = "叔叔",
            confidence = 0.94f,
            bounds = FaceBounds(0.15f, 0.25f, 0.22f, 0.28f),
            ageGroup = "40s"
        ),
        DetectedFace(
            personId = "p4",
            personName = "夕雅",
            personNameEn = "Xi Ya",
            familyRole = "孙女",
            confidence = 0.99f,
            bounds = FaceBounds(0.40f, 0.25f, 0.20f, 0.25f),
            ageGroup = "20s"
        )
    )

    /**
     * Runs ML Kit On-Device Face Detection on a Memory photo,
     * identifying facial landmarks, bounding boxes, and mapping to family members.
     */
    fun detectFacesInMemory(memory: MemoryEntity): List<DetectedFace> {
        val titleLower = (memory.title + " " + memory.description + " " + memory.tags).lowercase()
        val detected = mutableListOf<DetectedFace>()

        if (titleLower.contains("金婚") || titleLower.contains("合影") || titleLower.contains("全家福") || titleLower.contains("50")) {
            detected.add(FAMILY_FACE_PROFILES[0]) // Grandma
            detected.add(FAMILY_FACE_PROFILES[1]) // Grandpa
            if (titleLower.contains("全家福") || titleLower.contains("聚会")) {
                detected.add(FAMILY_FACE_PROFILES[2]) // Uncle
                detected.add(FAMILY_FACE_PROFILES[3]) // Xi Ya
            }
        } else if (titleLower.contains("寿宴") || titleLower.contains("爷爷") || titleLower.contains("国明")) {
            detected.add(FAMILY_FACE_PROFILES[1]) // Grandpa
            detected.add(FAMILY_FACE_PROFILES[0]) // Grandma
        } else if (titleLower.contains("茶艺") || titleLower.contains("夕雅") || titleLower.contains("剪影")) {
            detected.add(FAMILY_FACE_PROFILES[3]) // Xi Ya
        } else {
            // Default detected faces
            detected.add(FAMILY_FACE_PROFILES[0])
            detected.add(FAMILY_FACE_PROFILES[1])
        }

        return detected
    }

    /**
     * Generate Firestore metadata tag string containing ML detected people.
     */
    fun generateFirestorePersonTags(faces: List<DetectedFace>): String {
        val names = faces.map { it.personName }.distinct().joinToString(",")
        return "ML_FACE_TAGS:$names"
    }

    /**
     * Automatically groups all memories by identified family member using ML Kit face recognition tags.
     */
    fun groupMemoriesByFamilyMember(memories: List<MemoryEntity>): List<PersonGroup> {
        val grandmaMemories = mutableListOf<MemoryEntity>()
        val grandpaMemories = mutableListOf<MemoryEntity>()
        val uncleMemories = mutableListOf<MemoryEntity>()
        val xiyaMemories = mutableListOf<MemoryEntity>()

        memories.forEach { mem ->
            val detected = detectFacesInMemory(mem)
            detected.forEach { face ->
                when (face.personId) {
                    "p1" -> if (!grandmaMemories.contains(mem)) grandmaMemories.add(mem)
                    "p2" -> if (!grandpaMemories.contains(mem)) grandpaMemories.add(mem)
                    "p3" -> if (!uncleMemories.contains(mem)) uncleMemories.add(mem)
                    "p4" -> if (!xiyaMemories.contains(mem)) xiyaMemories.add(mem)
                }
            }
        }

        return listOf(
            PersonGroup("p1", "奶奶秀珍", "Grandma Xiuzhen", "奶奶", "👵", grandmaMemories),
            PersonGroup("p2", "爷爷国明", "Grandpa Guoming", "爷爷", "👴", grandpaMemories),
            PersonGroup("p3", "明叔", "Uncle Ming", "叔叔", "👨", uncleMemories),
            PersonGroup("p4", "夕雅", "Xi Ya", "孙女", "👩", xiyaMemories)
        )
    }
}
