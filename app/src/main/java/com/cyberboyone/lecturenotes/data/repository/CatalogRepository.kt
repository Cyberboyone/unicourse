package com.cyberboyone.lecturenotes.data.repository

import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty

/** Backed by the Supabase catalog, cached locally so browsing works offline. */
interface CatalogRepository {
    suspend fun getFaculties(): List<Faculty>
    suspend fun getDepartments(facultyId: String): List<Department>
    suspend fun getCourses(departmentId: String): List<Course>
    suspend fun searchCourses(query: String): List<Course>
    suspend fun getCourse(courseId: String): Course?
    /** Refreshes the local cache from Supabase when online. Safe to call opportunistically. */
    suspend fun refreshCatalog()
}
