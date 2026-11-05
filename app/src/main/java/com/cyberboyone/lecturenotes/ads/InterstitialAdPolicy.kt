package com.cyberboyone.lecturenotes.ads

import android.content.Context
import android.content.SharedPreferences

/**
 * Decides when to show an interstitial, per the spec:
 *   - shown after the 1st download
 *   - then after every 2 further downloads (3rd, 6th, 9th, ...)
 *   - never inside the reader (callers only invoke this from list/detail screens)
 *   - capped so two interstitials can't fire within [MIN_GAP_MS] of each other,
 *     in case a student downloads several courses back to back
 *
 * Download count persists across sessions (SharedPreferences) so a student who
 * returns daily keeps the same cadence rather than it resetting each launch.
 */
class InterstitialAdPolicy(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ad_policy", Context.MODE_PRIVATE)

    /** Call once, right after a download finishes. Returns true if an interstitial should show now. */
    fun onDownloadCompleted(): Boolean {
        val count = prefs.getInt(KEY_DOWNLOAD_COUNT, 0) + 1
        prefs.edit().putInt(KEY_DOWNLOAD_COUNT, count).apply()

        // 1st download, then every 3rd after that: 1, 3, 6, 9, 12, ...
        val isTriggerDownload = count == 1 || (count >= 3 && count % 3 == 0)
        if (!isTriggerDownload) return false

        val lastShownAt = prefs.getLong(KEY_LAST_SHOWN_AT, 0L)
        val now = System.currentTimeMillis()
        if (now - lastShownAt < MIN_GAP_MS) return false

        prefs.edit().putLong(KEY_LAST_SHOWN_AT, now).apply()
        return true
    }

    companion object {
        private const val KEY_DOWNLOAD_COUNT = "download_count"
        private const val KEY_LAST_SHOWN_AT = "last_shown_at"
        private const val MIN_GAP_MS = 3 * 60 * 1000L // 3 minutes
    }
}
