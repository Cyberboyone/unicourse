package com.cyberboyone.lecturenotes.data.local

import android.content.Context
import android.util.Base64
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Encrypts downloaded course PDFs with AES-256-GCM into app-private storage.
 *
 * Uses software AES-256-GCM with a persistent device-bound key to avoid
 * hardware TEE buffer overflow issues that cause AEADBadTagException on large
 * PDFs (>64KB) on various Android OEM chipsets (MediaTek, Exynos, etc.).
 *
 * Also includes fallback support for files encrypted with the legacy AndroidKeyStore.
 */
class EncryptedFileManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("app_secure_keystore", Context.MODE_PRIVATE)

    @Synchronized
    private fun getOrCreateDeviceKey(): SecretKey {
        val existingBase64 = prefs.getString(KEY_CIPHER_SECRET, null)
        if (!existingBase64.isNullOrBlank()) {
            val raw = Base64.decode(existingBase64, Base64.NO_WRAP)
            return SecretKeySpec(raw, "AES")
        }

        // Generate a cryptographically secure 256-bit AES key
        val keyBytes = ByteArray(32)
        SecureRandom().nextBytes(keyBytes)
        val encoded = Base64.encodeToString(keyBytes, Base64.NO_WRAP)
        prefs.edit().putString(KEY_CIPHER_SECRET, encoded).commit()
        return SecretKeySpec(keyBytes, "AES")
    }

    /** Legacy hardware Keystore key accessor for backward compatibility */
    private fun getLegacyKey(): SecretKey? {
        return runCatching {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            ks.getKey(KEY_ALIAS, null) as? SecretKey
        }.getOrNull()
    }

    /** Writes [plainBytes] as an encrypted file under app-private storage. Returns the file path. */
    fun saveEncrypted(courseId: String, version: Int, plainBytes: ByteArray): String {
        val key = getOrCreateDeviceKey()
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(plainBytes)

        val file = fileFor(courseId, version)
        // Store format: [1 byte: magic version 0x02][1 byte: IV length][IV][ciphertext]
        file.outputStream().use { out ->
            out.write(MAGIC_V2.toInt())
            out.write(iv.size)
            out.write(iv)
            out.write(encrypted)
        }
        return file.absolutePath
    }

    /** Decrypts an on-disk file into memory for the reader. Never writes plaintext to disk. */
    fun readDecrypted(path: String): ByteArray {
        val file = File(path)
        if (!file.exists() || file.length() < 16) {
            throw IllegalStateException("Downloaded lecture note file is missing or incomplete.")
        }
        val bytes = file.readBytes()

        // Check if file uses v2 format (starts with 0x02)
        if (bytes[0] == MAGIC_V2) {
            val ivLength = bytes[1].toInt() and 0xFF
            val iv = bytes.copyOfRange(2, 2 + ivLength)
            val ciphertext = bytes.copyOfRange(2 + ivLength, bytes.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateDeviceKey(), GCMParameterSpec(128, iv))
            return cipher.doFinal(ciphertext)
        }

        // Try standard device key with 4-byte header (v1 structure)
        val v1Attempt = runCatching {
            val ivLength = bytesToInt(bytes.copyOfRange(0, 4))
            val iv = bytes.copyOfRange(4, 4 + ivLength)
            val ciphertext = bytes.copyOfRange(4 + ivLength, bytes.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateDeviceKey(), GCMParameterSpec(128, iv))
            cipher.doFinal(ciphertext)
        }
        if (v1Attempt.isSuccess) {
            return v1Attempt.getOrThrow()
        }

        // Try legacy AndroidKeyStore key
        val legacyKey = getLegacyKey()
        if (legacyKey != null) {
            val legacyAttempt = runCatching {
                val ivLength = bytesToInt(bytes.copyOfRange(0, 4))
                val iv = bytes.copyOfRange(4, 4 + ivLength)
                val ciphertext = bytes.copyOfRange(4 + ivLength, bytes.size)

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, legacyKey, GCMParameterSpec(128, iv))
                cipher.doFinal(ciphertext)
            }
            if (legacyAttempt.isSuccess) {
                return legacyAttempt.getOrThrow()
            }
        }

        throw IllegalStateException("This note was downloaded with an older key session. Please tap 'Re-download' to refresh.")
    }

    fun deleteDownload(courseId: String, version: Int) {
        fileFor(courseId, version).delete()
        val dir = File(context.filesDir, "downloads")
        if (dir.exists()) {
            dir.listFiles { _, name -> name.startsWith("$courseId.") || name.startsWith(courseId) }?.forEach {
                runCatching { it.delete() }
            }
        }
    }

    private fun fileFor(courseId: String, version: Int): File {
        val dir = File(context.filesDir, "downloads").apply { mkdirs() }
        return File(dir, "$courseId.v$version.enc")
    }

    private fun bytesToInt(bytes: ByteArray) =
        ((bytes[0].toInt() and 0xFF) shl 24) or
        ((bytes[1].toInt() and 0xFF) shl 16) or
        ((bytes[2].toInt() and 0xFF) shl 8) or
        (bytes[3].toInt() and 0xFF)

    companion object {
        private const val MAGIC_V2: Byte = 0x02
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "lecture_notes_file_key"
        private const val KEY_CIPHER_SECRET = "device_bound_cipher_key_v2"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
