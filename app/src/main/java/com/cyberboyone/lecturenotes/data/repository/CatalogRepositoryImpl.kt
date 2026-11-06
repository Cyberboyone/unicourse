package com.cyberboyone.lecturenotes.data.repository

import android.content.Context
import com.cyberboyone.lecturenotes.data.local.CatalogDao
import com.cyberboyone.lecturenotes.data.local.CourseEntity
import com.cyberboyone.lecturenotes.data.local.DepartmentEntity
import com.cyberboyone.lecturenotes.data.local.FacultyEntity
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.data.remote.CatalogRemoteDataSource
import com.cyberboyone.lecturenotes.data.remote.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Reads from the Room cache first (so browsing works 100% offline),
 * pre-seeds from bundled assets on first run, and opportunistically
 * syncs with Supabase when internet is available.
 */
class CatalogRepositoryImpl(
    private val remote: CatalogRemoteDataSource,
    private val dao: CatalogDao,
    private val context: Context
) : CatalogRepository {

    override suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val existing = dao.getFaculties()
        if (existing.isNotEmpty()) return@withContext

        runCatching {
            val jsonStr = context.assets.open("catalog_seed.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonStr)

            val facArray = root.getJSONArray("faculties")
            val facList = mutableListOf<FacultyEntity>()
            for (i in 0 until facArray.length()) {
                val obj = facArray.getJSONObject(i)
                facList.add(
                    FacultyEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        coverUrl = if (obj.isNull("cover_url")) null else obj.optString("cover_url")
                    )
                )
            }

            val deptArray = root.getJSONArray("departments")
            val deptList = mutableListOf<DepartmentEntity>()
            for (i in 0 until deptArray.length()) {
                val obj = deptArray.getJSONObject(i)
                deptList.add(
                    DepartmentEntity(
                        id = obj.getString("id"),
                        facultyId = obj.getString("faculty_id"),
                        name = obj.getString("name")
                    )
                )
            }

            val courseArray = root.getJSONArray("courses")
            val courseList = mutableListOf<CourseEntity>()
            for (i in 0 until courseArray.length()) {
                val obj = courseArray.getJSONObject(i)
                val rawCover = obj.optString("cover_url", "")
                val fullCover = if (rawCover.startsWith("http://") || rawCover.startsWith("https://")) {
                    rawCover
                } else if (rawCover.isNotBlank()) {
                    "${NetworkModule.R2_PUBLIC_BASE_URL.trimEnd('/')}/${rawCover.trimStart('/')}"
                } else ""

                courseList.add(
                    CourseEntity(
                        id = obj.getString("id"),
                        departmentId = obj.getString("department_id"),
                        title = obj.getString("title"),
                        coverUrl = fullCover,
                        latestVersion = obj.optInt("latest_version", 1),
                        fileSizeBytes = obj.optLong("file_size_bytes", 0L)
                    )
                )
            }

            dao.insertFaculties(facList)
            dao.insertDepartments(deptList)
            dao.insertCourses(courseList)
        }
        Unit
    }

    override suspend fun getFaculties(): List<Faculty> = withContext(Dispatchers.IO) {
        seedIfNeeded()
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
        seedIfNeeded()
        val cached = dao.getDepartments(facultyId).map { Department(it.id, it.facultyId, it.name) }
        if (cached.isNotEmpty()) return@withContext cached

        runCatching {
            val remoteDepts = remote.getDepartments(facultyId)
            if (remoteDepts.isNotEmpty()) {
                dao.insertDepartments(remoteDepts.map { DepartmentEntity(it.id, it.facultyId, it.name) })
                remoteDepts
            } else cached
        }.getOrDefault(cached)
    }

    override suspend fun getCourses(departmentId: String): List<Course> = withContext(Dispatchers.IO) {
        seedIfNeeded()
        val cached = dao.getCourses(departmentId).map(::toCourse)
        if (cached.isNotEmpty()) return@withContext cached

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
        seedIfNeeded()
        val local = dao.searchCourses(query).map(::toCourse)
        if (local.isNotEmpty()) return@withContext local
        runCatching { remote.searchCourses(query) }.getOrDefault(emptyList())
    }

    override suspend fun getCourse(courseId: String): Course? = withContext(Dispatchers.IO) {
        seedIfNeeded()
        dao.getCourse(courseId)?.let(::toCourse)
            ?: runCatching { remote.getCourse(courseId) }.getOrNull()
    }

    override suspend fun getPopularCourses(limit: Int): List<Course> = withContext(Dispatchers.IO) {
        seedIfNeeded()
        dao.getPopularCourses(limit).map(::toCourse)
    }

    override suspend fun getAllCourses(): List<Course> = withContext(Dispatchers.IO) {
        seedIfNeeded()
        dao.getAllCourses().map(::toCourse)
    }

    override suspend fun getDepartmentCourseCount(departmentId: String): Int = withContext(Dispatchers.IO) {
        seedIfNeeded()
        dao.getCourseCountForDepartment(departmentId)
    }

    override suspend fun getTotalCourseCount(): Int = withContext(Dispatchers.IO) {
        seedIfNeeded()
        dao.getTotalCourseCount()
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
