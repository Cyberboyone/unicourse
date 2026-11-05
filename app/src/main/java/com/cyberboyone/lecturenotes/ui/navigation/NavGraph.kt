package com.cyberboyone.lecturenotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyberboyone.lecturenotes.ui.screens.course.CourseListScreen
import com.cyberboyone.lecturenotes.ui.screens.coursedetail.CourseDetailScreen
import com.cyberboyone.lecturenotes.ui.screens.department.DepartmentListScreen
import com.cyberboyone.lecturenotes.ui.screens.downloads.MyDownloadsScreen
import com.cyberboyone.lecturenotes.ui.screens.faculty.FacultyListScreen
import com.cyberboyone.lecturenotes.ui.screens.reader.ReaderScreen
import com.cyberboyone.lecturenotes.ui.screens.search.SearchScreen

object Routes {
    const val FACULTIES = "faculties"
    const val DEPARTMENTS = "departments/{facultyId}"
    const val COURSES = "courses/{departmentId}"
    const val SEARCH = "search"
    const val COURSE_DETAIL = "course/{courseId}"
    const val READER = "reader/{courseId}"
    const val DOWNLOADS = "downloads"

    fun departments(facultyId: String) = "departments/$facultyId"
    fun courses(departmentId: String) = "courses/$departmentId"
    fun courseDetail(courseId: String) = "course/$courseId"
    fun reader(courseId: String) = "reader/$courseId"
}

@Composable
fun LectureNotesNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.FACULTIES) {
        composable(Routes.FACULTIES) { FacultyListScreen(navController) }
        composable(Routes.DEPARTMENTS) { backStackEntry ->
            val facultyId = backStackEntry.arguments?.getString("facultyId").orEmpty()
            DepartmentListScreen(navController, facultyId)
        }
        composable(Routes.COURSES) { backStackEntry ->
            val departmentId = backStackEntry.arguments?.getString("departmentId").orEmpty()
            CourseListScreen(navController, departmentId)
        }
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(Routes.COURSE_DETAIL) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId").orEmpty()
            CourseDetailScreen(navController, courseId)
        }
        // Reader is intentionally reached only from CourseDetail's "Open" action,
        // never from a list screen directly — keeps ad logic out of this screen entirely.
        composable(Routes.READER) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId").orEmpty()
            ReaderScreen(courseId)
        }
        composable(Routes.DOWNLOADS) { MyDownloadsScreen(navController) }
    }
}
