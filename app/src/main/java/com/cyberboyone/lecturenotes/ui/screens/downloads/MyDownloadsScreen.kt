package com.cyberboyone.lecturenotes.ui.screens.downloads

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.viewmodel.MyDownloadsViewModel

@Composable
fun MyDownloadsScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: MyDownloadsViewModel = viewModel(factory = AppViewModelFactory(container))
    val rows by viewModel.rows.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("My Downloads") }) }) { padding ->
        AdSlottedGrid(
            items = rows,
            modifier = Modifier.padding(padding),
            cardContent = { row ->
                Card(
                    onClick = { navController.navigate(Routes.courseDetail(row.downloaded.courseId)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        (row.course?.title ?: row.downloaded.courseId) +
                            if (row.updateAvailable) "  •  New version available" else "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            },
            adContent = { NativeAdCard(modifier = Modifier.padding(8.dp)) }
        )
    }
}
