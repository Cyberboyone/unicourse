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

        // Refresh the catalog cache on launch so browsing is current when online,
        // and still works from the last cache when it isn't.
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { container.catalogRepository.refreshCatalog() }
        }
    }
}
