package com.cyberboyone.lecturenotes.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.cyberboyone.lecturenotes.ui.viewmodel.*

/**
 * One factory for every ViewModel in the app. Screens that need a route
 * argument (facultyId, courseId, ...) pass it through; the factory doesn't
 * know about Compose or navigation at all, so it stays trivially testable.
 */
class AppViewModelFactory(
    private val container: AppContainer,
    private val arg: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when (modelClass) {
        FacultyListViewModel::class.java ->
            FacultyListViewModel(container.catalogRepository) as T
        DepartmentListViewModel::class.java ->
            DepartmentListViewModel(container.catalogRepository, requireArg()) as T
        CourseListViewModel::class.java ->
            CourseListViewModel(container.catalogRepository, requireArg()) as T
        SearchViewModel::class.java ->
            SearchViewModel(container.catalogRepository) as T
        CourseDetailViewModel::class.java ->
            CourseDetailViewModel(
                container.catalogRepository, container.downloadRepository,
                container.interstitialAdPolicy, requireArg()
            ) as T
        MyDownloadsViewModel::class.java ->
            MyDownloadsViewModel(container.catalogRepository, container.downloadRepository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }

    private fun requireArg() = arg ?: error("AppViewModelFactory needs an arg for $this")
}
