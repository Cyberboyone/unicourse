package com.cyberboyone.lecturenotes.data.repository

import com.cyberboyone.lecturenotes.data.local.DownloadDao
import com.cyberboyone.lecturenotes.data.local.DownloadedCourseEntity
import com.cyberboyone.lecturenotes.data.local.EncryptedFileManager
import com.cyberboyone.lecturenotes.data.model.DownloadedCourse

/**
 * Downloads a course's PDF from R2, encrypts it into app-private storage,
 * and records it locally so the reader and the update-badge check can find it.
 */
class DownloadRepository(
    private val dao: DownloadDao,
    private val fileManager: EncryptedFileManager,
    private val r2Client: suspend (courseId: String, version: Int) -> ByteArray
) {
    suspend fun getDownloadedCourses(): List<DownloadedCourse> =
        dao.getAll().map {
            DownloadedCourse(it.courseId, it.downloadedVersion, it.localEncryptedPath, it.downloadedAtEpochMs)
        }

    suspend fun isDownloaded(courseId: String): DownloadedCourse? =
        dao.get(courseId)?.let {
            DownloadedCourse(it.courseId, it.downloadedVersion, it.localEncryptedPath, it.downloadedAtEpochMs)
        }

    /** Downloads and encrypts a course version. Call [onDownloadCompleted][com.cyberboyone.lecturenotes.ads.InterstitialAdPolicy.onDownloadCompleted] after this returns. */
    suspend fun downloadCourse(courseId: String, version: Int): DownloadedCourse {
        val plainBytes = r2Client(courseId, version)
        val path = fileManager.saveEncrypted(courseId, version, plainBytes)
        val entity = DownloadedCourseEntity(
            courseId = courseId,
            downloadedVersion = version,
            localEncryptedPath = path,
            downloadedAtEpochMs = System.currentTimeMillis()
        )
        dao.upsert(entity)
        return DownloadedCourse(courseId, version, path, entity.downloadedAtEpochMs)
    }

    /** Downloads and encrypts a course version, returning the decrypted bytes directly for immediate reading. */
    suspend fun reDownloadCourse(courseId: String, version: Int): ByteArray {
        val plainBytes = r2Client(courseId, version)
        val path = fileManager.saveEncrypted(courseId, version, plainBytes)
        val entity = DownloadedCourseEntity(
            courseId = courseId,
            downloadedVersion = version,
            localEncryptedPath = path,
            downloadedAtEpochMs = System.currentTimeMillis()
        )
        dao.upsert(entity)
        return plainBytes
    }

    suspend fun removeDownload(courseId: String, version: Int) {
        fileManager.deleteDownload(courseId, version)
        dao.delete(courseId)
    }

    suspend fun deleteDownload(courseId: String) {
        val entity = dao.get(courseId)
        val version = entity?.downloadedVersion ?: 1
        fileManager.deleteDownload(courseId, version)
        dao.delete(courseId)
    }

    /** Decrypted bytes for the reader. Never persisted to disk. */
    fun readCourseBytes(localEncryptedPath: String): ByteArray =
        fileManager.readDecrypted(localEncryptedPath)
}
