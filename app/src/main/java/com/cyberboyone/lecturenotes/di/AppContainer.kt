package com.cyberboyone.lecturenotes.di

import android.content.Context
import com.cyberboyone.lecturenotes.ads.AdManager
import com.cyberboyone.lecturenotes.ads.InterstitialAdPolicy
import com.cyberboyone.lecturenotes.data.local.EncryptedFileManager
import com.cyberboyone.lecturenotes.data.local.LectureNotesDatabase
import com.cyberboyone.lecturenotes.data.remote.CatalogRemoteDataSource
import com.cyberboyone.lecturenotes.data.remote.NetworkModule
import com.cyberboyone.lecturenotes.data.remote.R2StorageService
import com.cyberboyone.lecturenotes.data.repository.CatalogRepository
import com.cyberboyone.lecturenotes.data.repository.CatalogRepositoryImpl
import com.cyberboyone.lecturenotes.data.repository.DownloadRepository

/**
 * Hand-rolled DI container (no Hilt, to keep this scaffold dependency-light).
 * One instance held by the Application subclass; screens reach it through
 * LocalAppContainer (see AppContainerProvider.kt) and a thin ViewModelFactory.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database = LectureNotesDatabase.get(appContext)
    private val r2 = R2StorageService(NetworkModule.r2Client)
    private val remoteDataSource = CatalogRemoteDataSource(NetworkModule.supabaseApi)
    private val fileManager = EncryptedFileManager(appContext)

    val catalogRepository: CatalogRepository =
        CatalogRepositoryImpl(remoteDataSource, database.catalogDao())

    val downloadRepository = DownloadRepository(
        dao = database.downloadDao(),
        fileManager = fileManager,
        r2Client = { courseId, version -> r2.downloadCoursePdf(courseId, version) }
    )

    val interstitialAdPolicy = InterstitialAdPolicy(appContext)
    val adManager = AdManager(appContext)
}
