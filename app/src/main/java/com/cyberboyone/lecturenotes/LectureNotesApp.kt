package com.cyberboyone.lecturenotes

import android.app.Application
import com.cyberboyone.lecturenotes.di.AppContainer
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LectureNotesApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        container = AppContainer(this)
        container.adManager.preloadInterstitial()

        // 1. Instantly seed from bundled assets so all 117 courses are available offline with 0 delay.
        // 2. Refresh the catalog cache on launch if online to keep data synced.
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { container.catalogRepository.seedIfNeeded() }
            runCatching { container.catalogRepository.refreshCatalog() }
        }
    }
}
