package com.cyberboyone.lecturenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cyberboyone.lecturenotes.data.local.AppThemeMode
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.navigation.LectureNotesNavGraph
import com.cyberboyone.lecturenotes.ui.theme.LectureNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as LectureNotesApp).container
        setContent {
            val themeMode by container.themePreference.themeMode.collectAsState()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
            }

            CompositionLocalProvider(LocalAppContainer provides container) {
                LectureNotesTheme(darkTheme = isDark) {
                    LectureNotesNavGraph()
                }
            }
        }
    }
}
