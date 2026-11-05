package com.cyberboyone.lecturenotes.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Thin REST client over Supabase's auto-generated PostgREST API.
 * Base URL + anon key are supplied at build time (see NetworkModule) — the
 * anon key is safe to ship in the app; it only grants read access to the
 * public catalog tables, never to the admin-only write path.
 */
data class FacultyDto(val id: String, val name: String, val cover_url: String?)
data class DepartmentDto(val id: String, val faculty_id: String, val name: String)
data class CourseDto(
    val id: String,
    val department_id: String,
    val title: String,
    val cover_url: String,
    val latest_version: Int,
    val file_size_bytes: Long
)

// PostgREST filters are passed as "column=operator.value", e.g. "id=eq.abc123"
// or "title=ilike.*calculus*" — callers build the "eq.xxx" / "ilike.*xxx*" string
// themselves (see CatalogRemoteDataSource) rather than passing raw values here.
interface SupabaseApi {
    @GET("rest/v1/faculties?select=id,name,cover_url&order=name")
    suspend fun getFaculties(): List<FacultyDto>

    @GET("rest/v1/departments?select=id,faculty_id,name&order=name")
    suspend fun getDepartments(@Query("faculty_id") facultyIdEq: String): List<DepartmentDto>

    @GET("rest/v1/courses?select=id,department_id,title,cover_url,latest_version,file_size_bytes&order=title")
    suspend fun getCourses(@Query("department_id") departmentIdEq: String): List<CourseDto>

    @GET("rest/v1/courses?select=id,department_id,title,cover_url,latest_version,file_size_bytes")
    suspend fun searchCourses(@Query("title") titleIlike: String): List<CourseDto>

    @GET("rest/v1/courses?select=id,department_id,title,cover_url,latest_version,file_size_bytes")
    suspend fun getCourse(@Query("id") idEq: String): List<CourseDto>
}
