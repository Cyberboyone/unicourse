package com.cyberboyone.lecturenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.navigation.LectureNotesNavGraph
import com.cyberboyone.lecturenotes.ui.theme.LectureNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as LectureNotesApp).container
        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                LectureNotesTheme {
                    LectureNotesNavGraph()
                }
            }
        }
    }
}
