package com.cyberboyone.lecturenotes.ui.screens.faculty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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

@Composable
fun FacultyListScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: FacultyListViewModel = viewModel(factory = AppViewModelFactory(container))
    val faculties by viewModel.faculties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            UniCourseTopBar(
                title = "Faculties & Disciplines",
                subtitle = "Select a faculty to browse departments",
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SEARCH) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ElectricViolet
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading && faculties.isEmpty() -> {
                    SkeletonGrid(count = 8, columns = 2)
                }
                faculties.isEmpty() -> {
                    ErrorStateView(
                        message = errorMessage ?: "No faculties found. Please tap retry.",
                        onRetry = { viewModel.loadFaculties() }
                    )
                }
                else -> {
                    AdSlottedGrid(
                        items = faculties,
                        modifier = Modifier.fillMaxSize(),
                        columns = 2,
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 90.dp),
                        cardContent = { faculty ->
                            FacultyGridCard(
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
fun FacultyGridCard(
    faculty: Faculty,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    ElectricViolet.copy(alpha = 0.12f),
                                    VibrantBlue.copy(alpha = 0.12f)
                                )
                            )
                        ),
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
                    tint = ElectricViolet,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

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
                    text = "View Departments →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricViolet,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
