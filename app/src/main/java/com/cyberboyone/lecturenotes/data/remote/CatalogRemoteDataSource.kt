package com.cyberboyone.lecturenotes.data.remote

import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty

/** Wraps [SupabaseApi], turning network DTOs into domain models and PostgREST filter syntax. */
class CatalogRemoteDataSource(private val api: SupabaseApi) {

    suspend fun getFaculties(): List<Faculty> =
        api.getFaculties().map { Faculty(it.id, it.name, it.cover_url) }

    suspend fun getDepartments(facultyId: String): List<Department> =
        api.getDepartments(facultyIdEq = "eq.$facultyId")
            .map { Department(it.id, it.faculty_id, it.name) }

    suspend fun getCourses(departmentId: String): List<Course> =
        api.getCourses(departmentIdEq = "eq.$departmentId").map(::toCourse)

    suspend fun searchCourses(query: String): List<Course> =
        api.searchCourses(titleIlike = "ilike.*${query.trim()}*").map(::toCourse)

    suspend fun getCourse(courseId: String): Course? =
        api.getCourse(idEq = "eq.$courseId").map(::toCourse).firstOrNull()

    private fun toCourse(dto: CourseDto): Course {
        val fullCoverUrl = if (dto.cover_url.startsWith("http://") || dto.cover_url.startsWith("https://")) {
            dto.cover_url
        } else {
            "${NetworkModule.R2_PUBLIC_BASE_URL.trimEnd('/')}/${dto.cover_url.trimStart('/')}"
        }
        return Course(
            id = dto.id,
            departmentId = dto.department_id,
            title = dto.title,
            coverUrl = fullCoverUrl,
            latestVersion = dto.latest_version,
            fileSizeBytes = dto.file_size_bytes
        )
    }
}
