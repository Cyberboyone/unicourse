package com.cyberboyone.lecturenotes.data.model

data class Faculty(
    val id: String,
    val name: String,
    val coverUrl: String? = null
)

data class Department(
    val id: String,
    val facultyId: String,
    val name: String
)

data class Course(
    val id: String,
    val departmentId: String,
    val title: String,
    val coverUrl: String,
    val latestVersion: Int,
    val fileSizeBytes: Long
)

/** What's actually stored on the device for a downloaded course. */
data class DownloadedCourse(
    val courseId: String,
    val downloadedVersion: Int,
    val localEncryptedPath: String,
    val downloadedAtEpochMs: Long
) {
    fun isUpdateAvailable(latestVersion: Int): Boolean = latestVersion > downloadedVersion
}
