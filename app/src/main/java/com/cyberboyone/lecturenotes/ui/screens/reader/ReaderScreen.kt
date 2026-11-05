package com.cyberboyone.lecturenotes.ui.screens.reader

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.theme.AccentGold
import com.cyberboyone.lecturenotes.ui.theme.NigeriaGreen
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * No ads here — ever. Sets FLAG_SECURE so the system blocks screenshots and
 * screen recording while a course is open, and offers no share/export
 * affordance anywhere on this screen. The decrypted PDF bytes live only in
 * memory for the lifetime of this composable; nothing is written to disk.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(courseId: String, onBack: () -> Unit = {}) {
    val container = LocalAppContainer.current
    val context = LocalContext.current

    DisposableEffect(Unit) {
        (context as? Activity)?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        onDispose {
            (context as? Activity)?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    var pdfBytes by remember { mutableStateOf<ByteArray?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(courseId) {
        val downloaded = container.downloadRepository.isDownloaded(courseId)
        if (downloaded == null) {
            error = "This course hasn't been downloaded yet."
            return@LaunchedEffect
        }
        pdfBytes = withContext(Dispatchers.Default) {
            runCatching { container.downloadRepository.readCourseBytes(downloaded.localEncryptedPath) }
                .getOrElse {
                    error = "Couldn't open this course: ${it.message}"
                    null
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Offline Lecture Reader",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NigeriaGreen)
            )
        }
    ) { padding ->
        when {
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            pdfBytes == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NigeriaGreen)
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "Decrypting lecture material...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                val bytes = pdfBytes!!
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    factory = { ctx -> PDFView(ctx, null) },
                    update = { pdfView ->
                        pdfView.fromBytes(bytes)
                            .enableSwipe(true)
                            .swipeHorizontal(false) // continuous vertical scroll, per spec
                            .fitEachPage(true)      // fit to width
                            .enableAntialiasing(true)
                            .load()
                    }
                )
            }
        }
    }
}
