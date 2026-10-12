package com.example.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import com.example.util.AesEncryptionUtil

class FirebaseFamilyService {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { 
        val instance = FirebaseFirestore.getInstance()
        try {
            val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            instance.firestoreSettings = settings
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firestore settings config issue", e)
        }
        instance
    }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (e: Exception) { null }

    val currentUserEmail: String
        get() = currentUser?.email ?: "xiya@family.com"

    // --- Firebase Auth Methods ---
    suspend fun registerUser(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Auth Register error", e)
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Auth Login error", e)
            Result.failure(e)
        }
    }

    fun signOutUser() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign out error", e)
        }
    }

    // --- Firebase Firestore Photo & Album Synchronization ---
    suspend fun saveMemoryToFirestore(memory: MemoryEntity): Boolean {
        return try {
            val memoryMap = hashMapOf(
                "id" to memory.id,
                "title" to AesEncryptionUtil.encrypt(memory.title),
                "titleEn" to AesEncryptionUtil.encrypt(memory.titleEn),
                "description" to AesEncryptionUtil.encrypt(memory.description),
                "descriptionEn" to AesEncryptionUtil.encrypt(memory.descriptionEn),
                "location" to memory.location,
                "locationEn" to memory.locationEn,
                "date" to memory.date,
                "cameraModel" to memory.cameraModel,
                "photoResNames" to memory.photoResNames,
                "likesCount" to memory.likesCount,
                "commentsCount" to memory.commentsCount,
                "rating" to memory.rating,
                "isEncrypted" to memory.isEncrypted,
                "tags" to memory.tags,
                "eventName" to memory.eventName,
                "eventNameEn" to memory.eventNameEn,
                "isHistoricalHighlight" to memory.isHistoricalHighlight,
                "isOfflineCached" to memory.isOfflineCached,
                "encryptionMode" to "AES-256-CBC",
                "updatedBy" to currentUserEmail,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("family_albums")
                .document(memory.id)
                .set(memoryMap)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Firestore save memory error", e)
            false
        }
    }

    fun observeFirestoreMemories(): Flow<List<MemoryEntity>> = callbackFlow {
        try {
            val listenerRegistration = firestore.collection("family_albums")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirebaseService", "Firestore observe error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                val rawTitle = doc.getString("title") ?: ""
                                val rawTitleEn = doc.getString("titleEn") ?: ""
                                val rawDesc = doc.getString("description") ?: ""
                                val rawDescEn = doc.getString("descriptionEn") ?: ""

                                MemoryEntity(
                                    id = doc.getString("id") ?: doc.id,
                                    title = AesEncryptionUtil.decrypt(rawTitle),
                                    titleEn = AesEncryptionUtil.decrypt(rawTitleEn),
                                    description = AesEncryptionUtil.decrypt(rawDesc),
                                    descriptionEn = AesEncryptionUtil.decrypt(rawDescEn),
                                    location = doc.getString("location") ?: "",
                                    locationEn = doc.getString("locationEn") ?: "",
                                    date = doc.getString("date") ?: "",
                                    cameraModel = doc.getString("cameraModel") ?: "",
                                    photoResNames = doc.getString("photoResNames") ?: "",
                                    likesCount = (doc.getLong("likesCount") ?: 0).toInt(),
                                    commentsCount = (doc.getLong("commentsCount") ?: 0).toInt(),
                                    rating = (doc.getDouble("rating") ?: 5.0).toFloat(),
                                    isEncrypted = doc.getBoolean("isEncrypted") ?: false,
                                    tags = doc.getString("tags") ?: "",
                                    eventName = doc.getString("eventName") ?: "",
                                    eventNameEn = doc.getString("eventNameEn") ?: "",
                                    isHistoricalHighlight = doc.getBoolean("isHistoricalHighlight") ?: false,
                                    isOfflineCached = true
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(list)
                    }
                }

            awaitClose { listenerRegistration.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Firestore callbackFlow failed setup", e)
            close(e)
        }
    }

    suspend fun updateMemoryTagsInFirestore(memoryId: String, newTags: String): Boolean {
        return try {
            firestore.collection("family_albums")
                .document(memoryId)
                .update("tags", newTags, "timestamp", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Firestore update tags error", e)
            false
        }
    }

    suspend fun addCommentToMemoryInFirestore(memoryId: String, author: String, commentText: String): Boolean {
        return try {
            val commentData = hashMapOf(
                "author" to author,
                "text" to AesEncryptionUtil.encrypt(commentText),
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("family_albums")
                .document(memoryId)
                .collection("comments")
                .add(commentData)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Firestore add comment error", e)
            false
        }
    }

    // --- Firebase Dynamic Links for Multi-Photo Family Sharing (Requirement 2) ---
    suspend fun generateFirebaseDynamicLink(
        memories: List<MemoryEntity>,
        title: String = "家庭珍贵照片精选",
        description: String = "点击查看四代同堂与金婚岁月回忆"
    ): String = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val ids = memories.joinToString(",") { it.id }
            val encodedIds = java.net.URLEncoder.encode(ids, "UTF-8")
            val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
            val encodedDesc = java.net.URLEncoder.encode(description, "UTF-8")

            val deepLink = "https://goldenmemory.app/share?ids=$encodedIds"
            val encodedDeepLink = java.net.URLEncoder.encode(deepLink, "UTF-8")

            // Firebase Dynamic Link URL construction
            val dynamicLink = "https://goldenmemory.page.link/?link=$encodedDeepLink" +
                    "&apn=com.aistudio.goldenmemory.fambook" +
                    "&st=$encodedTitle" +
                    "&sd=$encodedDesc" +
                    "&ofl=$encodedDeepLink"

            return@withContext dynamicLink
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error generating dynamic link", e)
            return@withContext "https://goldenmemory.page.link/share?ids=" + memories.joinToString(",") { it.id }
        }
    }
}
