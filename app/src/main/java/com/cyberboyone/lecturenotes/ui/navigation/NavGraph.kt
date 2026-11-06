package com.cyberboyone.lecturenotes.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.UniCourseBottomBar
import com.cyberboyone.lecturenotes.ui.screens.course.CourseListScreen
import com.cyberboyone.lecturenotes.ui.screens.coursedetail.CourseDetailScreen
import com.cyberboyone.lecturenotes.ui.screens.department.DepartmentListScreen
import com.cyberboyone.lecturenotes.ui.screens.downloads.MyDownloadsScreen
import com.cyberboyone.lecturenotes.ui.screens.faculty.FacultyListScreen
import com.cyberboyone.lecturenotes.ui.screens.home.HomeScreen
import com.cyberboyone.lecturenotes.ui.screens.profile.ProfileScreen
import com.cyberboyone.lecturenotes.ui.screens.reader.ReaderScreen
import com.cyberboyone.lecturenotes.ui.screens.search.SearchScreen
import com.cyberboyone.lecturenotes.util.NetworkUtils

object Routes {
    const val HOME = "home"
    const val FACULTIES = "faculties"
    const val DEPARTMENTS = "departments/{facultyId}"
    const val COURSES = "courses/{departmentId}"
    const val SEARCH = "search"
    const val COURSE_DETAIL = "course/{courseId}"
    const val READER = "reader/{courseId}"
    const val DOWNLOADS = "downloads"
    const val PROFILE = "profile"

    fun departments(facultyId: String) = "departments/$facultyId"
    fun courses(departmentId: String) = "courses/$departmentId"
    fun courseDetail(courseId: String) = "course/$courseId"
    fun reader(courseId: String) = "reader/$courseId"
}

@Composable
fun LectureNotesNavGraph(navController: NavHostController = rememberNavController()) {
    val container = LocalAppContainer.current
    val context = LocalContext.current

    var hasDownloads by remember { mutableStateOf(false) }
    var downloadsCount by remember { mutableIntStateOf(0) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val downloaded = container.downloadRepository.getDownloadedCourses()
        downloadsCount = downloaded.size
        hasDownloads = downloaded.isNotEmpty()
        isInitialized = true
    }

    if (!isInitialized) return

    val isOnline = NetworkUtils.isOnline(context)
    // Offline redirect: when a user already has downloaded materials and opens app in offline mode,
    // take them directly to their downloaded materials!
    val startDestination = if (!isOnline && hasDownloads) Routes.DOWNLOADS else Routes.HOME

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation in Reader and Search to maximize reading/search experience
    val showBottomBar = currentRoute != null &&
            !currentRoute.startsWith("reader") &&
            currentRoute != Routes.SEARCH

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                UniCourseBottomBar(
                    currentRoute = currentRoute,
                    downloadsCount = downloadsCount,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.HOME) { HomeScreen(navController) }
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
            composable(Routes.READER) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId").orEmpty()
                ReaderScreen(courseId, onBack = { navController.popBackStack() })
            }
            composable(Routes.DOWNLOADS) { MyDownloadsScreen(navController) }
            composable(Routes.PROFILE) { ProfileScreen(navController) }
        }
    }
}
