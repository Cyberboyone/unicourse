package com.cyberboyone.lecturenotes.ui.screens.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.*
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.*
import com.cyberboyone.lecturenotes.ui.viewmodel.CourseListViewModel

@Composable
fun CourseListScreen(navController: NavHostController, departmentId: String) {
    val container = LocalAppContainer.current
    val viewModel: CourseListViewModel =
        viewModel(factory = AppViewModelFactory(container, arg = departmentId))
    val courses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var downloadedIds by remember { mutableStateOf(emptySet<String>()) }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        val dls = container.downloadRepository.getDownloadedCourses()
        downloadedIds = dls.map { it.courseId }.toSet()
    }

    val filteredCourses = remember(courses, selectedFilter, downloadedIds) {
        when (selectedFilter) {
            "Downloaded" -> courses.filter { downloadedIds.contains(it.id) }
            "Available" -> courses.filter { !downloadedIds.contains(it.id) }
            else -> courses
        }
    }

    Scaffold(
        topBar = {
            UniCourseTopBar(
                title = "Course Materials",
                subtitle = "Download & study 100% offline",
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filter Chips
            UniFilterChipRow(
                filters = listOf("All", "Downloaded", "Available"),
                selected = selectedFilter,
                onSelected = { selectedFilter = it },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && courses.isEmpty() -> {
                        SkeletonGrid(count = 6, columns = 2)
                    }
                    courses.isEmpty() -> {
                        ErrorStateView(
                            message = errorMessage ?: "No materials found for this department.",
                            onRetry = { viewModel.loadCourses() }
                        )
                    }
                    filteredCourses.isEmpty() -> {
                        EmptyStateView(
                            emoji = "📂",
                            title = "No Courses in this Filter",
                            subtitle = if (selectedFilter == "Downloaded") "You haven't downloaded any courses in this department yet."
                            else "All courses are already downloaded!"
                        )
                    }
                    else -> {
                        AdSlottedGrid(
                            items = filteredCourses,
                            modifier = Modifier.fillMaxSize(),
                            columns = 2,
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 90.dp),
                            cardContent = { course ->
                                CourseCard(
                                    course = course,
                                    isDownloaded = downloadedIds.contains(course.id),
                                    onClick = { navController.navigate(Routes.courseDetail(course.id)) }
                                )
                            },
                            adContent = { NativeAdCard() }
                        )
                    }
                }
            }
        }
    }
}
