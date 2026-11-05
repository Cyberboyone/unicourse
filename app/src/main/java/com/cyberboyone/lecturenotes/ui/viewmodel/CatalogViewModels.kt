package com.cyberboyone.lecturenotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberboyone.lecturenotes.data.model.Course
import com.cyberboyone.lecturenotes.data.model.Department
import com.cyberboyone.lecturenotes.data.model.Faculty
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FacultyListViewModel(private val repo: CatalogRepository) : ViewModel() {
    private val _faculties = MutableStateFlow<List<Faculty>>(emptyList())
    val faculties: StateFlow<List<Faculty>> = _faculties.asStateFlow()

    init {
        viewModelScope.launch { _faculties.value = repo.getFaculties() }
    }
}

class DepartmentListViewModel(private val repo: CatalogRepository, facultyId: String) : ViewModel() {
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    init {
        viewModelScope.launch { _departments.value = repo.getDepartments(facultyId) }
    }
}

class CourseListViewModel(private val repo: CatalogRepository, departmentId: String) : ViewModel() {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    init {
        viewModelScope.launch { _courses.value = repo.getCourses(departmentId) }
    }
}

class SearchViewModel(private val repo: CatalogRepository) : ViewModel() {
    private val _results = MutableStateFlow<List<Course>>(emptyList())
    val results: StateFlow<List<Course>> = _results.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _results.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300) // debounce
            _results.value = repo.searchCourses(query)
        }
    }
}
