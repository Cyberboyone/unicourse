package com.cyberboyone.lecturenotes.ui.components

import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.cyberboyone.lecturenotes.ads.AdManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var adLoaded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { nativeAd?.destroy() }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "SPONSORED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                factory = { context ->
                    val frameLayout = FrameLayout(context)
                    val adLoader = AdLoader.Builder(context, AdManager.NATIVE_AD_UNIT_ID)
                        .forNativeAd { ad ->
                            nativeAd = ad
                            adLoaded = true
                        }
                        .withAdListener(object : AdListener() {})
                        .build()
                    adLoader.loadAd(AdRequest.Builder().build())
                    frameLayout
                },
                update = { container ->
                    val ad = nativeAd ?: return@AndroidView
                    val context = container.context
                    val adView = NativeAdView(context)
                    val headline = TextView(context).apply {
                        text = ad.headline
                        textSize = 14f
                        setTextColor(android.graphics.Color.DKGRAY)
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    adView.addView(headline)
                    adView.headlineView = headline
                    adView.setNativeAd(ad)

                    container.removeAllViews()
                    container.addView(adView)
                }
            )

            if (!adLoaded) {
                Column(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎓 Study Partner",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Empowering Nigerian Students",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
