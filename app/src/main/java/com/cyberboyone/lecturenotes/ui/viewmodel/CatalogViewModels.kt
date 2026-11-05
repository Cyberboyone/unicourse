package com.cyberboyone.lecturenotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FacultyListViewModel(private val repo: CatalogRepository) : ViewModel() {
    private val _faculties = MutableStateFlow<List<Faculty>>(emptyList())
    val faculties: StateFlow<List<Faculty>> = _faculties.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadFaculties()
    }

    fun loadFaculties() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = repo.getFaculties()
                _faculties.value = list
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load faculties"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class DepartmentListViewModel(private val repo: CatalogRepository, private val facultyId: String) : ViewModel() {
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadDepartments()
    }

    fun loadDepartments() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = repo.getDepartments(facultyId)
                _departments.value = list
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load departments"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CourseListViewModel(private val repo: CatalogRepository, private val departmentId: String) : ViewModel() {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = repo.getCourses(departmentId)
                _courses.value = list
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load courses"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SearchViewModel(private val repo: CatalogRepository) : ViewModel() {
    private val _results = MutableStateFlow<List<Course>>(emptyList())
    val results: StateFlow<List<Course>> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _results.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300) // debounce
            try {
                _results.value = repo.searchCourses(query)
            } finally {
                _isSearching.value = false
            }
        }
    }
}
