package com.cyberboyone.lecturenotes.ui.components

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cyberboyone.lecturenotes.ads.AdManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * The native ad card used by every list screen's AdSlottedGrid. Wraps
 * AdMob's View-based NativeAdView via AndroidView, since Compose has no
 * native equivalent.
 */
@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(Unit) {
        onDispose { nativeAd?.destroy() }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth().height(120.dp),
        factory = { context ->
            val adLoader = AdLoader.Builder(context, AdManager.NATIVE_AD_UNIT_ID)
                .forNativeAd { ad -> nativeAd = ad }
                .withAdListener(object : AdListener() {})
                .build()
            adLoader.loadAd(AdRequest.Builder().build())

            // Minimal inflated layout bound at render time; a real build would
            // use a proper XML native-ad layout (headline/body/icon/CTA views).
            FrameLayout(context)
        },
        update = { container ->
            val ad = nativeAd ?: return@AndroidView
            val context = container.context
            val adView = NativeAdView(context)
            val headline = android.widget.TextView(context).apply { text = ad.headline }
            adView.addView(headline)
            adView.headlineView = headline
            adView.setNativeAd(ad)

            container.removeAllViews()
            container.addView(adView)
        }
    )
}
