package com.cyberboyone.lecturenotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberboyone.lecturenotes.ads.InterstitialAdPolicy
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.DownloadedCourse
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import com.cyberboyone.lecturenotes.data.repository.DownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DownloadState {
    data object Idle : DownloadState()
    data object Downloading : DownloadState()
    data class Downloaded(val shouldShowInterstitial: Boolean) : DownloadState()
    data class Failed(val message: String) : DownloadState()
}

class CourseDetailViewModel(
    private val catalogRepo: CatalogRepository,
    private val downloadRepo: DownloadRepository,
    private val interstitialAdPolicy: InterstitialAdPolicy,
    courseId: String
) : ViewModel() {

    private val _course = MutableStateFlow<Course?>(null)
    val course: StateFlow<Course?> = _course.asStateFlow()

    private val _downloaded = MutableStateFlow<DownloadedCourse?>(null)
    val downloaded: StateFlow<DownloadedCourse?> = _downloaded.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    init {
        viewModelScope.launch {
            _course.value = catalogRepo.getCourse(courseId)
            _downloaded.value = downloadRepo.isDownloaded(courseId)
        }
    }

    fun download(courseId: String) {
        val latestVersion = _course.value?.latestVersion ?: return
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading
            runCatching { downloadRepo.downloadCourse(courseId, latestVersion) }
                .onSuccess { downloaded ->
                    _downloaded.value = downloaded
                    // The ONE place the interstitial cadence (1st, then every 2 more) is triggered.
                    val shouldShow = interstitialAdPolicy.onDownloadCompleted()
                    _downloadState.value = DownloadState.Downloaded(shouldShowInterstitial = shouldShow)
                }
                .onFailure { e ->
                    _downloadState.value = DownloadState.Failed(e.message ?: "Download failed")
                }
        }
    }
}
