package com.cyberboyone.lecturenotes.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Downloads course PDFs and cover images from the public Cloudflare R2
 * bucket, matching the layout from the spec:
 *   covers/{course_id}.jpg
 *   notes/{course_id}/v{version}.pdf
 *
 * Returns raw bytes; encryption happens on-device afterward via
 * EncryptedFileManager — R2 itself does not need to store ciphertext.
 */
class R2StorageService(
    private val client: OkHttpClient,
    baseUrl: String = NetworkModule.R2_PUBLIC_BASE_URL
) {
    private val normalizedBaseUrl = baseUrl.trimEnd('/') + "/"

    suspend fun downloadCoursePdf(courseId: String, version: Int): ByteArray =
        withContext(Dispatchers.IO) { fetch("${normalizedBaseUrl}notes/$courseId/v$version.pdf") }

    suspend fun downloadCover(courseId: String): ByteArray =
        withContext(Dispatchers.IO) { fetch("${normalizedBaseUrl}covers/$courseId.jpg") }

    fun coverUrl(courseId: String): String = "${normalizedBaseUrl}covers/$courseId.jpg"

    private fun fetch(url: String): ByteArray {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("R2 fetch failed (${response.code}) for $url")
            }
            return response.body?.bytes() ?: throw IOException("Empty body for $url")
        }
    }
}
