package com.cyberboyone.lecturenotes.data.local

import androidx.room.*

@Entity(tableName = "downloaded_courses")
data class DownloadedCourseEntity(
    @PrimaryKey val courseId: String,
    val downloadedVersion: Int,
    val localEncryptedPath: String,
    val downloadedAtEpochMs: Long
)

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloaded_courses")
    suspend fun getAll(): List<DownloadedCourseEntity>

    @Query("SELECT * FROM downloaded_courses WHERE courseId = :courseId")
    suspend fun get(courseId: String): DownloadedCourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DownloadedCourseEntity)

    @Query("DELETE FROM downloaded_courses WHERE courseId = :courseId")
    suspend fun delete(courseId: String)
}

@Database(
    entities = [
        DownloadedCourseEntity::class,
        FacultyEntity::class,
        DepartmentEntity::class,
        CourseEntity::class
    ],
    version = 1
)
abstract class LectureNotesDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun catalogDao(): CatalogDao

    companion object {
        @Volatile private var instance: LectureNotesDatabase? = null

        fun get(context: android.content.Context): LectureNotesDatabase =
            instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    LectureNotesDatabase::class.java,
                    "lecture_notes.db"
                ).build().also { instance = it }
            }
    }
}
