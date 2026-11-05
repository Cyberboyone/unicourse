package com.cyberboyone.lecturenotes.data.repository

import com.cyberboyone.lecturenotes.data.local.CatalogDao
import com.cyberboyone.lecturenotes.data.local.CourseEntity
import com.cyberboyone.lecturenotes.data.local.DepartmentEntity
import com.cyberboyone.lecturenotes.data.local.FacultyEntity
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.data.remote.CatalogRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads from the Room cache first (so browsing works offline), and
 * opportunistically refreshes it from Supabase. [refreshCatalog] is the
 * only place that writes — call it on app start and on pull-to-refresh.
 */
class CatalogRepositoryImpl(
    private val remote: CatalogRemoteDataSource,
    private val dao: CatalogDao
) : CatalogRepository {

    override suspend fun getFaculties(): List<Faculty> = withContext(Dispatchers.IO) {
        dao.getFaculties().map { Faculty(it.id, it.name, it.coverUrl) }
    }

    override suspend fun getDepartments(facultyId: String): List<Department> = withContext(Dispatchers.IO) {
        dao.getDepartments(facultyId).map { Department(it.id, it.facultyId, it.name) }
    }

    override suspend fun getCourses(departmentId: String): List<Course> = withContext(Dispatchers.IO) {
        dao.getCourses(departmentId).map(::toCourse)
    }

    override suspend fun searchCourses(query: String): List<Course> = withContext(Dispatchers.IO) {
        // Local cache first for instant, offline-friendly results.
        val local = dao.searchCourses(query).map(::toCourse)
        if (local.isNotEmpty()) return@withContext local
        // Fall back to a live Supabase search if the cache has nothing (e.g. right after install).
        runCatching { remote.searchCourses(query) }.getOrDefault(emptyList())
    }

    override suspend fun getCourse(courseId: String): Course? = withContext(Dispatchers.IO) {
        dao.getCourse(courseId)?.let(::toCourse)
            ?: runCatching { remote.getCourse(courseId) }.getOrNull()
    }

    override suspend fun refreshCatalog() = withContext(Dispatchers.IO) {
        // Fetch everything from the network first; only touch the local cache
        // once all three network calls have succeeded, so a mid-refresh
        // network failure can't leave the cache partially cleared.
        val faculties = remote.getFaculties()
        val departments = faculties.flatMap { remote.getDepartments(it.id) }
        val courses = departments.flatMap { remote.getCourses(it.id) }

        dao.replaceCatalog(
            faculties = faculties.map { FacultyEntity(it.id, it.name, it.coverUrl) },
            departments = departments.map { DepartmentEntity(it.id, it.facultyId, it.name) },
            courses = courses.map {
                CourseEntity(it.id, it.departmentId, it.title, it.coverUrl, it.latestVersion, it.fileSizeBytes)
            }
        )
    }

    private fun toCourse(entity: CourseEntity) = Course(
        entity.id, entity.departmentId, entity.title, entity.coverUrl,
        entity.latestVersion, entity.fileSizeBytes
    )
}
