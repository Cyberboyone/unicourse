package com.cyberboyone.lecturenotes.ui.screens.reader

import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.GradientButton
import com.cyberboyone.lecturenotes.ui.theme.*
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * High-security offline PDF Reader:
 * - Window FLAG_SECURE: blocks screenshots and screen recording
 * - In-memory only decryption: zero plaintext bytes written to disk
 * - Zero ads: strict educational reader guarantee
 * - Continuous vertical scroll with page counter & fit-to-width
 * - Automatic one-tap re-download recovery if old key or corrupted file
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(courseId: String, onBack: () -> Unit = {}) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
    var courseTitle by remember { mutableStateOf(courseId.uppercase()) }
    var courseVersion by remember { mutableIntStateOf(1) }
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(0) }
    var isPdfReady by remember { mutableStateOf(false) }
    var isReDownloading by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        val course = container.catalogRepository.getCourse(courseId)
        if (course != null) {
            courseTitle = course.title
            courseVersion = course.latestVersion
        }

        val downloaded = container.downloadRepository.isDownloaded(courseId)
        if (downloaded == null) {
            error = "This course hasn't been downloaded yet."
            return@LaunchedEffect
        }
        courseVersion = downloaded.downloadedVersion

        pdfBytes = withContext(Dispatchers.Default) {
            runCatching { container.downloadRepository.readCourseBytes(downloaded.localEncryptedPath) }
                .getOrElse { ex ->
                    val msg = ex.message ?: ex.localizedMessage ?: ex::class.java.simpleName
                    error = "Couldn't decrypt note: $msg"
                    null
                }
        }
    }

    fun triggerReDownload() {
        scope.launch {
            isReDownloading = true
            error = null
            try {
                val bytes = withContext(Dispatchers.IO) {
                    container.downloadRepository.reDownloadCourse(courseId, courseVersion)
                }
                pdfBytes = bytes
            } catch (e: Exception) {
                val msg = e.message ?: e.localizedMessage ?: e::class.java.simpleName
                error = "Re-download failed: $msg. Please check your internet connection."
            } finally {
                isReDownloading = false
            }
        }
    }

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
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = NeonLime,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "OFFLINE SECURE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonLime
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = courseTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepPurple)
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
                isReDownloading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = ElectricViolet,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "Re-downloading and decrypting...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Applying fresh device encryption key",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(SoftYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔐", fontSize = 38.sp)
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "Note Needs Refresh",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "This note was encrypted with an older session key. Tap below to re-download with the updated encryption key.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        GradientButton(
                            text = "🔄 Re-download Note & Open",
                            onClick = { triggerReDownload() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
                pdfBytes == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = ElectricViolet,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = "Decrypting lecture material...",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Verifying AES-256 hardware key",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    val bytes = pdfBytes!!
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PDFView(ctx, null).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                fromBytes(bytes)
                                    .enableSwipe(true)
                                    .swipeHorizontal(false)
                                    .enableDoubletap(true)
                                    .defaultPage(0)
                                    .enableAnnotationRendering(false)
                                    .spacing(12)
                                    .fitEachPage(true)
                                    .enableAntialiasing(true)
                                    .onLoad { nbPages ->
                                        totalPages = nbPages
                                        isPdfReady = true
                                    }
                                    .onPageChange { page, _ ->
                                        currentPage = page + 1
                                    }
                                    .onError { t ->
                                        val msg = t.message ?: t.localizedMessage ?: t::class.java.simpleName
                                        error = "Failed to render PDF: $msg"
                                    }
                                    .load()
                            }
                        }
                    )

                    // Page Indicator Floating Pill
                    AnimatedVisibility(
                        visible = isPdfReady && totalPages > 0,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Page $currentPage / $totalPages",
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
}
