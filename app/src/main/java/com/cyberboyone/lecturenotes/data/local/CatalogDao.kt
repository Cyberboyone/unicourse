package com.cyberboyone.lecturenotes.data.local

import androidx.room.*

@Entity(tableName = "faculties")
data class FacultyEntity(@PrimaryKey val id: String, val name: String, val coverUrl: String?)

@Entity(tableName = "departments")
data class DepartmentEntity(@PrimaryKey val id: String, val facultyId: String, val name: String)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val departmentId: String,
    val title: String,
    val coverUrl: String,
    val latestVersion: Int,
    val fileSizeBytes: Long
)

@Dao
interface CatalogDao {
    @Query("SELECT * FROM faculties ORDER BY name")
    suspend fun getFaculties(): List<FacultyEntity>

    @Query("SELECT * FROM departments WHERE facultyId = :facultyId ORDER BY name")
    suspend fun getDepartments(facultyId: String): List<DepartmentEntity>

    @Query("SELECT * FROM courses WHERE departmentId = :departmentId ORDER BY title")
    suspend fun getCourses(departmentId: String): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' ORDER BY title")
    suspend fun searchCourses(query: String): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourse(courseId: String): CourseEntity?

    @Query("DELETE FROM faculties")
    suspend fun clearFaculties()

    @Query("DELETE FROM departments")
    suspend fun clearDepartments()

    @Query("DELETE FROM courses")
    suspend fun clearCourses()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaculties(items: List<FacultyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(items: List<DepartmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(items: List<CourseEntity>)

    /**
     * Replaces the whole catalog atomically. Without this, a refresh that's
     * interrupted mid-way (connection drops between clearing and
     * re-inserting a table) could leave the offline cache partially empty —
     * exactly the case this app most needs to survive.
     */
    @Transaction
    suspend fun replaceCatalog(
        faculties: List<FacultyEntity>,
        departments: List<DepartmentEntity>,
        courses: List<CourseEntity>
    ) {
        clearFaculties(); insertFaculties(faculties)
        clearDepartments(); insertDepartments(departments)
        clearCourses(); insertCourses(courses)
    }
}
