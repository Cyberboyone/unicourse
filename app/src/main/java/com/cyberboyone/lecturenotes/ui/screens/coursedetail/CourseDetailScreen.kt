package com.cyberboyone.lecturenotes.ui.screens.coursedetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.viewmodel.CourseDetailViewModel
import com.cyberboyone.lecturenotes.ui.viewmodel.DownloadState

/**
 * No ads on this screen by design — it sits between the lists (which carry
 * native ads) and the reader (which carries none), and is also where the
 * interstitial decision fires, right after a download completes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(navController: NavHostController, courseId: String) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val viewModel: CourseDetailViewModel =
        viewModel(factory = AppViewModelFactory(container, arg = courseId))

    val course by viewModel.course.collectAsState()
    val downloaded by viewModel.downloaded.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()

    val updateAvailable = course != null && downloaded?.isUpdateAvailable(course!!.latestVersion) == true

    // Fires the interstitial right here, outside the reader, once the download finishes —
    // matches the spec: shown when the student returns to browsing after a download.
    LaunchedEffect(downloadState) {
        val state = downloadState
        if (state is DownloadState.Downloaded && state.shouldShowInterstitial) {
            (context as? android.app.Activity)?.let { activity ->
                container.adManager.showInterstitialIfReady(activity)
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(course?.title ?: "Course") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            course?.coverUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = course?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(bottom = 12.dp)
                )
            }

            if (updateAvailable) {
                AssistChip(
                    onClick = { viewModel.download(courseId) },
                    label = { Text("New version available — tap to update") },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            val isDownloading = downloadState is DownloadState.Downloading
            Button(
                onClick = {
                    if (downloaded != null && !updateAvailable) {
                        navController.navigate(Routes.reader(courseId))
                    } else {
                        viewModel.download(courseId)
                    }
                },
                enabled = !isDownloading
            ) {
                Text(
                    when {
                        isDownloading -> "Downloading..."
                        downloaded != null && !updateAvailable -> "Open"
                        else -> "Download"
                    }
                )
            }

            if (downloadState is DownloadState.Failed) {
                Text(
                    (downloadState as DownloadState.Failed).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
