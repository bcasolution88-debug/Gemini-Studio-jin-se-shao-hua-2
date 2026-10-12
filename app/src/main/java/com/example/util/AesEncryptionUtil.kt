package com.example.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesEncryptionUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY_SEED = "GoldenFamilyMemorySecretKey2026AES256"
    private val IV = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

    private fun getSecretKey(): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(SECRET_KEY_SEED.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(bytes, "AES")
    }

    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return plainText
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            "AES256:" + Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    fun decrypt(cipherText: String): String {
        if (!cipherText.startsWith("AES256:")) return cipherText
        val cleanCipher = cipherText.removePrefix("AES256:")
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), ivSpec)
            val decodedBytes = Base64.decode(cleanCipher, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            cipherText
        }
    }
}
