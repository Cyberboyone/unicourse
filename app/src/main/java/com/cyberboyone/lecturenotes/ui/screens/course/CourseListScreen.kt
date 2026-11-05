package com.cyberboyone.lecturenotes.ui.screens.course

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.viewmodel.CourseListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(navController: NavHostController, departmentId: String) {
    val container = LocalAppContainer.current
    val viewModel: CourseListViewModel =
        viewModel(factory = AppViewModelFactory(container, arg = departmentId))
    val courses by viewModel.courses.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Courses") }) }) { padding ->
        AdSlottedGrid(
            items = courses,
            modifier = Modifier.padding(padding),
            cardContent = { course ->
                // Only title + cover shown here — the full PDF opens from CourseDetail, never from this card.
                Card(
                    onClick = { navController.navigate(Routes.courseDetail(course.id)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = course.coverUrl,
                            contentDescription = course.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        )
                        Text(course.title, modifier = Modifier.padding(12.dp))
                    }
                }
            },
            adContent = { NativeAdCard(modifier = Modifier.padding(8.dp)) }
        )
    }
}
