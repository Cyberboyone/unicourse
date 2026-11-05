package com.cyberboyone.lecturenotes.ui.screens.reader

import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * No ads here — ever. Sets FLAG_SECURE so the system blocks screenshots and
 * screen recording while a course is open, and offers no share/export
 * affordance anywhere on this screen. The decrypted PDF bytes live only in
 * memory for the lifetime of this composable; nothing is written to disk.
 */
@Composable
fun ReaderScreen(courseId: String) {
    val container = LocalAppContainer.current
    val context = LocalContext.current

    DisposableEffect(Unit) {
        (context as? android.app.Activity)?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        onDispose {
            (context as? android.app.Activity)?.window?.clearFlags(
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

    Scaffold { padding ->
        when {
            error != null -> Text(error!!, modifier = Modifier.fillMaxSize().padding(padding))
            pdfBytes == null -> androidx.compose.foundation.layout.Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            else -> {
                // pdfBytes is a `var`, so Kotlin won't smart-cast it to non-null inside a
                // closure that AndroidView stores and re-invokes later — snapshot it to a
                // local val first.
                val bytes = pdfBytes!!
                AndroidView(
                    modifier = Modifier.fillMaxSize().padding(padding),
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
