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
 * opportunistically fetches from Supabase when the cache is empty.
 */
class CatalogRepositoryImpl(
    private val remote: CatalogRemoteDataSource,
    private val dao: CatalogDao
) : CatalogRepository {

    override suspend fun getFaculties(): List<Faculty> = withContext(Dispatchers.IO) {
        val cached = dao.getFaculties().map { Faculty(it.id, it.name, it.coverUrl) }
        if (cached.isNotEmpty()) return@withContext cached

        // Cache empty: fetch immediately from Supabase and cache it
        runCatching {
            val remoteFaculties = remote.getFaculties()
            if (remoteFaculties.isNotEmpty()) {
                dao.insertFaculties(remoteFaculties.map { FacultyEntity(it.id, it.name, it.coverUrl) })
                remoteFaculties
            } else cached
        }.getOrDefault(cached)
    }

    override suspend fun getDepartments(facultyId: String): List<Department> = withContext(Dispatchers.IO) {
        val cached = dao.getDepartments(facultyId).map { Department(it.id, it.facultyId, it.name) }
        if (cached.isNotEmpty()) return@withContext cached

        // Cache empty: fetch immediately from Supabase and cache it
        runCatching {
            val remoteDepts = remote.getDepartments(facultyId)
            if (remoteDepts.isNotEmpty()) {
                dao.insertDepartments(remoteDepts.map { DepartmentEntity(it.id, it.facultyId, it.name) })
                remoteDepts
            } else cached
        }.getOrDefault(cached)
    }

    override suspend fun getCourses(departmentId: String): List<Course> = withContext(Dispatchers.IO) {
        val cached = dao.getCourses(departmentId).map(::toCourse)
        if (cached.isNotEmpty()) return@withContext cached

        // Cache empty: fetch immediately from Supabase and cache it
        runCatching {
            val remoteCourses = remote.getCourses(departmentId)
            if (remoteCourses.isNotEmpty()) {
                dao.insertCourses(remoteCourses.map {
                    CourseEntity(it.id, it.departmentId, it.title, it.coverUrl, it.latestVersion, it.fileSizeBytes)
                })
                remoteCourses
            } else cached
        }.getOrDefault(cached)
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
        runCatching {
            val faculties = remote.getFaculties()
            if (faculties.isNotEmpty()) {
                dao.insertFaculties(faculties.map { FacultyEntity(it.id, it.name, it.coverUrl) })
            }
            for (f in faculties) {
                val depts = runCatching { remote.getDepartments(f.id) }.getOrDefault(emptyList())
                if (depts.isNotEmpty()) {
                    dao.insertDepartments(depts.map { DepartmentEntity(it.id, it.facultyId, it.name) })
                    for (d in depts) {
                        val courses = runCatching { remote.getCourses(d.id) }.getOrDefault(emptyList())
                        if (courses.isNotEmpty()) {
                            dao.insertCourses(courses.map {
                                CourseEntity(it.id, it.departmentId, it.title, it.coverUrl, it.latestVersion, it.fileSizeBytes)
                            })
                        }
                    }
                }
            }
        }
        Unit
    }

    private fun toCourse(entity: CourseEntity) = Course(
        entity.id, entity.departmentId, entity.title, entity.coverUrl,
        entity.latestVersion, entity.fileSizeBytes
    )
}
