package com.cyberboyone.lecturenotes.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypts a downloaded course PDF with a device-bound AES key held in the
 * Android Keystore, and stores it under the app's private files directory
 * (never the public Downloads folder, never shared via "open with").
 *
 * The key never leaves the Keystore, so a raw copy of the encrypted file
 * pulled off a rooted device is unreadable without this app's keystore entry.
 */
class EncryptedFileManager(private val context: Context) {

    private val keystore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun getOrCreateKey(): SecretKey {
        (keystore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /** Writes [plainBytes] as an encrypted file under app-private storage. Returns the file path. */
    fun saveEncrypted(courseId: String, version: Int, plainBytes: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainBytes)

        val file = fileFor(courseId, version)
        // Store IV alongside ciphertext: [4-byte IV length][IV][ciphertext]
        file.outputStream().use { out ->
            out.write(intToBytes(iv.size))
            out.write(iv)
            out.write(encrypted)
        }
        return file.absolutePath
    }

    /** Decrypts an on-disk file into memory for the reader. Never writes plaintext to disk. */
    fun readDecrypted(path: String): ByteArray {
        val bytes = File(path).readBytes()
        val ivLength = bytesToInt(bytes.copyOfRange(0, 4))
        val iv = bytes.copyOfRange(4, 4 + ivLength)
        val ciphertext = bytes.copyOfRange(4 + ivLength, bytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(ciphertext)
    }

    fun deleteDownload(courseId: String, version: Int) {
        fileFor(courseId, version).delete()
    }

    private fun fileFor(courseId: String, version: Int): File {
        val dir = File(context.filesDir, "downloads").apply { mkdirs() }
        return File(dir, "$courseId.v$version.enc")
    }

    private fun intToBytes(value: Int) = byteArrayOf(
        (value shr 24).toByte(), (value shr 16).toByte(),
        (value shr 8).toByte(), value.toByte()
    )

    private fun bytesToInt(bytes: ByteArray) =
        (bytes[0].toInt() shl 24) or (bytes[1].toInt() and 0xFF shl 16) or
        (bytes[2].toInt() and 0xFF shl 8) or (bytes[3].toInt() and 0xFF)

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "lecture_notes_file_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
