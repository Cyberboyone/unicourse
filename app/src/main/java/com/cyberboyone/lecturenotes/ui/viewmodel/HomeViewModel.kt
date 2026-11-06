package com.cyberboyone.lecturenotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import com.cyberboyone.lecturenotes.data.repository.DownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val catalogRepo: CatalogRepository,
    private val downloadRepo: DownloadRepository
) : ViewModel() {

    private val _faculties = MutableStateFlow<List<Faculty>>(emptyList())
    val faculties: StateFlow<List<Faculty>> = _faculties.asStateFlow()

    private val _popularCourses = MutableStateFlow<List<Course>>(emptyList())
    val popularCourses: StateFlow<List<Course>> = _popularCourses.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadedCourseRow>>(emptyList())
    val downloads: StateFlow<List<DownloadedCourseRow>> = _downloads.asStateFlow()

    private val _totalCourses = MutableStateFlow(117)
    val totalCourses: StateFlow<Int> = _totalCourses.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                catalogRepo.seedIfNeeded()
                _faculties.value = catalogRepo.getFaculties()
                _popularCourses.value = catalogRepo.getPopularCourses(6)
                _totalCourses.value = catalogRepo.getTotalCourseCount()

                val dlList = downloadRepo.getDownloadedCourses()
                _downloads.value = dlList.map { dl ->
                    DownloadedCourseRow(dl, catalogRepo.getCourse(dl.courseId))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
