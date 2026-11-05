package com.cyberboyone.lecturenotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.DownloadedCourse
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import com.cyberboyone.lecturenotes.data.repository.DownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** A downloaded course paired with its catalog entry, so the badge can compare versions. */
data class DownloadedCourseRow(val downloaded: DownloadedCourse, val course: Course?) {
    val updateAvailable: Boolean
        get() = course != null && downloaded.isUpdateAvailable(course.latestVersion)
}

class MyDownloadsViewModel(
    private val catalogRepo: CatalogRepository,
    private val downloadRepo: DownloadRepository
) : ViewModel() {

    private val _rows = MutableStateFlow<List<DownloadedCourseRow>>(emptyList())
    val rows: StateFlow<List<DownloadedCourseRow>> = _rows.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val downloads = downloadRepo.getDownloadedCourses()
            _rows.value = downloads.map { dl ->
                DownloadedCourseRow(dl, catalogRepo.getCourse(dl.courseId))
            }
        }
    }
}
