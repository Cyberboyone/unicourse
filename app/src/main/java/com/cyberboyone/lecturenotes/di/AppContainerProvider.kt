package com.cyberboyone.lecturenotes.di

import androidx.compose.runtime.staticCompositionLocalOf

/** Makes the single [AppContainer] reachable from any Composable without passing it down manually. */
val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided — wrap the app content in CompositionLocalProvider(LocalAppContainer provides container)")
}
