package com.cyberboyone.lecturenotes.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.*
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.theme.*
import com.cyberboyone.lecturenotes.ui.viewmodel.DownloadedCourseRow
import com.cyberboyone.lecturenotes.ui.viewmodel.MyDownloadsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun MyDownloadsScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: MyDownloadsViewModel = viewModel(factory = AppViewModelFactory(container))
    val rows by viewModel.rows.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var totalStorageBytes by remember { mutableLongStateOf(0L) }
    var itemToDelete by remember { mutableStateOf<DownloadedCourseRow?>(null) }

    LaunchedEffect(rows) {
        totalStorageBytes = withContext(Dispatchers.IO) {
            rows.sumOf { r ->
                val f = File(r.downloaded.localEncryptedPath)
                if (f.exists()) f.length() else 0L
            }
        }
    }

    val filteredRows = remember(rows, searchQuery) {
        if (searchQuery.isBlank()) rows
        else {
            val q = searchQuery.trim().lowercase()
            rows.filter { r ->
                r.downloaded.courseId.lowercase().contains(q) ||
                (r.course?.title?.lowercase()?.contains(q) == true)
            }
        }
    }

    itemToDelete?.let { row ->
        val code = extractCourseCode(row.downloaded.courseId, row.course?.title.orEmpty())
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Downloaded Note?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete $code from your offline storage? You can re-download it anytime for free.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val toDelete = row
                        itemToDelete = null
                        viewModel.deleteDownload(toDelete.downloaded.courseId, toDelete.downloaded.downloadedVersion)
                    }
                ) {
                    Text("Delete", color = Coral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            UniCourseTopBar(
                title = "My Offline Notes",
                subtitle = "${rows.size} notes saved • ${if (totalStorageBytes > 0) formatFileSize(totalStorageBytes) else "0 KB"}"
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
                EmptyStateView(
                    emoji = "📥",
                    title = "No Downloaded Notes Yet",
                    subtitle = "Download course materials to study 100% offline without needing mobile data or WiFi.",
                    actionLabel = "Browse Faculties",
                    onAction = { navController.navigate(Routes.FACULTIES) }
                )
            } else {
                // Search Within Downloads
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter your saved notes...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ElectricViolet
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )

                AdSlottedGrid(
                    items = filteredRows,
                    modifier = Modifier.fillMaxSize(),
                    columns = 1,
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 90.dp),
                    cardContent = { row ->
                        DownloadedCourseCard(
                            row = row,
                            onOpen = { navController.navigate(Routes.reader(row.downloaded.courseId)) },
                            onDetail = { navController.navigate(Routes.courseDetail(row.downloaded.courseId)) },
                            onDelete = { itemToDelete = row }
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
    onOpen: () -> Unit,
    onDetail: () -> Unit,
    onDelete: () -> Unit
) {
    val courseCode = extractCourseCode(row.downloaded.courseId, row.course?.title.orEmpty())

    Card(
        onClick = onDetail,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ElectricViolet.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📖", fontSize = 24.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricViolet)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = courseCode,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    if (row.updateAvailable) {
                        Spacer(Modifier.width(6.dp))
                        UpdateAvailableBadge()
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = row.course?.title ?: row.downloaded.courseId.uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
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
                        text = "Encrypted v${row.downloaded.downloadedVersion}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete note",
                    tint = Coral,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(4.dp))

            Button(
                onClick = onOpen,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Read",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
