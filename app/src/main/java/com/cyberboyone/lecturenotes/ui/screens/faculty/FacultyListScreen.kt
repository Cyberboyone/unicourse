package com.cyberboyone.lecturenotes.ui.screens.faculty

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.AccentGold
import com.cyberboyone.lecturenotes.ui.theme.NigeriaGreen
import com.cyberboyone.lecturenotes.ui.theme.NigeriaGreenDark
import com.cyberboyone.lecturenotes.ui.theme.NigeriaGreenLight
import com.cyberboyone.lecturenotes.ui.viewmodel.FacultyListViewModel

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
fun FacultyListScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: FacultyListViewModel = viewModel(factory = AppViewModelFactory(container))
    val faculties by viewModel.faculties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "UniCourse Nigeria",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Text(
                            text = "All University Lecture Materials",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SEARCH) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { navController.navigate(Routes.DOWNLOADS) }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "My Downloads",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NigeriaGreen
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(NigeriaGreenDark, NigeriaGreen, NigeriaGreenLight)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇳🇬 Nigerian Universities Hub",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = AccentGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "100% Free",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Curated Lecture Notes & Handouts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Select a faculty below to access verified course materials.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            // Main Content Area
            when {
                isLoading && faculties.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = NigeriaGreen,
                                strokeWidth = 3.dp
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Connecting to university catalog...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                faculties.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "📚",
                                fontSize = 48.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No Faculties Available",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "Please check your internet connection to sync the courses.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = { viewModel.loadFaculties() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NigeriaGreen
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Retry Sync")
                            }
                        }
                    }
                }
                else -> {
                    AdSlottedGrid(
                        items = faculties,
                        modifier = Modifier.fillMaxSize(),
                        columns = 2,
                        contentPadding = PaddingValues(12.dp),
                        cardContent = { faculty ->
                            FacultyCard(
                                faculty = faculty,
                                onClick = { navController.navigate(Routes.departments(faculty.id)) }
                            )
                        },
                        adContent = { NativeAdCard() }
                    )
                }
            }
        }
    }
}

@Composable
fun FacultyCard(
    faculty: Faculty,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getFacultyIcon(faculty.name),
                        fontSize = 24.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = NigeriaGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = faculty.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "View Departments",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NigeriaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
