package com.cyberboyone.lecturenotes.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Loads and shows interstitials. The cadence decision (should one show at
 * all) lives in [InterstitialAdPolicy]; this class only handles the AdMob
 * mechanics once that policy says yes.
 *
 * Replace INTERSTITIAL_AD_UNIT_ID with the real unit before release — this
 * is Google's public test ID, safe to ship during development only.
 */
class AdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null

    fun preloadInterstitial() {
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    /** Shows an interstitial. If preloaded, shows immediately; otherwise loads on-demand and shows. */
    fun showInterstitialIfReady(activity: Activity, onDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial()
                    onDismissed()
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    preloadInterstitial()
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            // Not ready yet: load and show on-demand so user definitely sees the ad
            InterstitialAd.load(
                activity,
                INTERSTITIAL_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: InterstitialAd) {
                        loadedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                preloadInterstitial()
                                onDismissed()
                            }
                            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                                interstitialAd = null
                                preloadInterstitial()
                                onDismissed()
                            }
                        }
                        loadedAd.show(activity)
                    }
                    override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                        interstitialAd = null
                        onDismissed()
                    }
                }
            )
        }
    }

    companion object {
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9529770421530115/4062730942"
        const val NATIVE_AD_UNIT_ID = "ca-app-pub-9529770421530115/2286614844"
        const val BANNER_AD_UNIT_ID = "ca-app-pub-9529770421530115/2177757450"
    }
}
