package com.cyberboyone.lecturenotes.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.AccentGold
import com.cyberboyone.lecturenotes.ui.theme.NigeriaGreen
import com.cyberboyone.lecturenotes.ui.viewmodel.DownloadedCourseRow
import com.cyberboyone.lecturenotes.ui.viewmodel.MyDownloadsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDownloadsScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: MyDownloadsViewModel = viewModel(factory = AppViewModelFactory(container))
    val rows by viewModel.rows.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Downloads",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Accessible 100% offline",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📥", fontSize = 38.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No Downloads Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Any lecture notes you download will be securely saved here for reading without internet.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate(Routes.FACULTIES) },
                            colors = ButtonDefaults.buttonColors(containerColor = NigeriaGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "📚", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("Browse Faculties")
                        }
                    }
                }
            } else {
                AdSlottedGrid(
                    items = rows,
                    modifier = Modifier.fillMaxSize(),
                    columns = 1,
                    contentPadding = PaddingValues(14.dp),
                    cardContent = { row ->
                        DownloadedCourseCard(
                            row = row,
                            onClick = { navController.navigate(Routes.courseDetail(row.downloaded.courseId)) }
                        )
                    },
                    adContent = { NativeAdCard() }
                )
            }
        }
    }
}

@Composable
fun DownloadedCourseCard(
    row: DownloadedCourseRow,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NigeriaGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📖", fontSize = 24.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.course?.title ?: row.downloaded.courseId,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NigeriaGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Saved • v${row.downloaded.downloadedVersion}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NigeriaGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (row.updateAvailable) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "• Update available",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            TextButton(onClick = onClick) {
                Text(
                    text = "Open",
                    color = NigeriaGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
