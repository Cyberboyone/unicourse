package com.cyberboyone.lecturenotes.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.*
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.*
import com.cyberboyone.lecturenotes.ui.viewmodel.HomeViewModel
import com.cyberboyone.lecturenotes.util.NetworkUtils
import java.util.Calendar

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private fun getFacultyIcon(name: String): String = when {
    name.contains("Engineer", ignoreCase = true) || name.contains("Tech", ignoreCase = true) -> "⚙️"
    name.contains("Scien", ignoreCase = true) || name.contains("Comput", ignoreCase = true) -> "🔬"
    name.contains("Medic", ignoreCase = true) || name.contains("Health", ignoreCase = true) -> "🩺"
    name.contains("Law", ignoreCase = true) || name.contains("Legal", ignoreCase = true) -> "⚖️"
    name.contains("Art", ignoreCase = true) || name.contains("Humanit", ignoreCase = true) -> "🎭"
    name.contains("Social", ignoreCase = true) || name.contains("Sociol", ignoreCase = true) -> "📊"
    name.contains("Manage", ignoreCase = true) || name.contains("Admin", ignoreCase = true) || name.contains("Busin", ignoreCase = true) -> "💼"
    name.contains("Educat", ignoreCase = true) -> "📚"
    name.contains("Agric", ignoreCase = true) || name.contains("Forest", ignoreCase = true) -> "🌾"
    name.contains("Pharm", ignoreCase = true) -> "💊"
    name.contains("Enviro", ignoreCase = true) -> "🌍"
    else -> "🎓"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(container))

    val faculties by viewModel.faculties.collectAsState()
    val popularCourses by viewModel.popularCourses.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val totalCourses by viewModel.totalCourses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isOnline = remember { NetworkUtils.isOnline(context) }
    val greeting = remember { getGreeting() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(PrimaryGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎓", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UniCourse",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "Nigerian University Hub",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Offline / Online Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isOnline) NeonLime.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF2E7D32) else Color(0xFFE65100))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) "ONLINE" else "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) Color(0xFF1B5E20) else Color(0xFFE65100)
                                )
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Personalized Greeting
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$greeting, Scholar! 👋",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Access verified lecture notes anytime, anywhere.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar Button (tappable pill)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { navController.navigate(Routes.SEARCH) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Search 117+ courses (e.g. GST 111, MTH 101)...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Hero Banner
            item {
                GradientHeroBanner(
                    title = "All Nigerian University Notes",
                    subtitle = "100% offline encrypted reading with zero internet required once downloaded.",
                    badge = "🇳🇬 $totalCourses COURSES • 9 FACULTIES",
                    gradient = HeroGradient
                )
            }

            // Quick Resume Study Card (if user has downloaded courses)
            if (downloads.isNotEmpty()) {
                val latestDownload = downloads.first()
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        SectionHeader(
                            title = "Resume Studying",
                            action = "View All (${downloads.size})",
                            onAction = { navController.navigate(Routes.DOWNLOADS) }
                        )

                        Card(
                            onClick = { navController.navigate(Routes.reader(latestDownload.downloaded.courseId)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(PrimaryGradient)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📖", fontSize = 24.sp)
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = latestDownload.course?.title ?: latestDownload.downloaded.courseId.uppercase(),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = NeonLime,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Ready to read offline",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ElectricViolet,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ElectricViolet)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Read",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Featured / Popular Notes Carousel
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    SectionHeader(
                        title = "Featured Lecture Notes",
                        action = "Explore",
                        onAction = { navController.navigate(Routes.FACULTIES) }
                    )

                    if (isLoading && popularCourses.isEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(3) {
                                CardSkeleton(modifier = Modifier.width(160.dp))
                            }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(popularCourses) { course ->
                                val isDownloaded = downloads.any { it.downloaded.courseId == course.id }
                                CourseCard(
                                    course = course,
                                    isDownloaded = isDownloaded,
                                    onClick = { navController.navigate(Routes.courseDetail(course.id)) },
                                    modifier = Modifier.width(170.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Browse Faculties Section - SHOW ALL 9 FACULTIES
            item {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    SectionHeader(
                        title = "Browse Faculties (${faculties.size})",
                        action = "Explore All",
                        onAction = { navController.navigate(Routes.FACULTIES) }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        faculties.forEach { faculty ->
                            HomeFacultyCard(
                                faculty = faculty,
                                onClick = { navController.navigate(Routes.departments(faculty.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFacultyCard(
    faculty: Faculty,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getFacultyIcon(faculty.name), fontSize = 22.sp)
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        text = faculty.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Explore departments & syllabi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = ElectricViolet,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
