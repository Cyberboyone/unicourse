package com.cyberboyone.lecturenotes.ui.screens.coursedetail

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.*
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.*
import com.cyberboyone.lecturenotes.ui.viewmodel.CourseDetailViewModel
import com.cyberboyone.lecturenotes.ui.viewmodel.DownloadState

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
    val courseCode = extractCourseCode(courseId, course?.title.orEmpty())

    LaunchedEffect(downloadState) {
        val state = downloadState
        if (state is DownloadState.Downloaded && state.shouldShowInterstitial) {
            (context as? Activity)?.let { activity ->
                container.adManager.showInterstitialIfReady(activity)
            }
        }
    }

    Scaffold(
        topBar = {
            UniCourseTopBar(
                title = "Course Details",
                subtitle = courseCode,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Book Cover Card with ContentScale.Fit - ZERO CROPPING!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ElectricViolet.copy(alpha = 0.08f),
                                    VibrantBlue.copy(alpha = 0.16f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val cover = course?.coverUrl.orEmpty()
                    if (cover.isNotBlank()) {
                        AsyncImage(
                            model = cover,
                            contentDescription = course?.title,
                            contentScale = ContentScale.Fit, // FULL COVER DISPLAYED
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📖", fontSize = 44.sp)
                        }
                    }

                    // Course Code Badge (Top-Start)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricViolet)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = courseCode,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    // Downloaded Status Badge (Top-End)
                    if (downloaded != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonLime)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF1E3A00),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "SAVED OFFLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1E3A00)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Course Title
            Column {
                Text(
                    text = course?.title ?: courseCode,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Official Nigerian University Lecture Note",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Metadata Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                course?.let { c ->
                    val size = formatFileSize(c.fileSizeBytes)
                    if (size.isNotBlank()) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Size: $size", fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text("Version ${c.latestVersion}", fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text("100% Offline", fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Security & Offline Reader Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Hardware-Encrypted Reader",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Encrypted with AES-256 in your device's Keystore. Screenshot protected with zero ads in reader.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Update Alert Card if applicable
            if (updateAvailable) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftYellow.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF6B5900)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "New Version Available",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B5900)
                                )
                            )
                            Text(
                                text = "An updated syllabus has been published.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B5900)
                            )
                        }
                        TextButton(onClick = { viewModel.download(courseId) }) {
                            Text("Update", fontWeight = FontWeight.Bold, color = ElectricViolet)
                        }
                    }
                }
            }

            // Primary Action Button (Download / Open Offline Reader)
            val isDownloading = downloadState is DownloadState.Downloading
            val isAlreadyDownloaded = downloaded != null && !updateAvailable

            GradientButton(
                text = when {
                    isDownloading -> "Downloading Lecture Note..."
                    isAlreadyDownloaded -> "📖 Open Offline Reader"
                    updateAvailable -> "🔄 Update Lecture Note"
                    else -> "📥 Download Note (100% Free)"
                },
                onClick = {
                    if (isAlreadyDownloaded) {
                        navController.navigate(Routes.reader(courseId))
                    } else {
                        viewModel.download(courseId)
                    }
                },
                enabled = !isDownloading && (isAlreadyDownloaded || course != null),
                gradient = if (isAlreadyDownloaded) listOf(ElectricViolet, DeepPurple) else PrimaryGradient,
                modifier = Modifier.fillMaxWidth()
            )

            // Animated Download Progress
            AnimatedVisibility(visible = isDownloading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DownloadProgressIndicator()
                }
            }

            // Error display
            if (downloadState is DownloadState.Failed) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (downloadState as DownloadState.Failed).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}
