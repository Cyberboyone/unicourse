package com.cyberboyone.lecturenotes.ui.screens.faculty

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.viewmodel.FacultyListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyListScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: FacultyListViewModel = viewModel(factory = AppViewModelFactory(container))
    val faculties by viewModel.faculties.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All University Lecture Notes") },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.DOWNLOADS) }) {
                        Text("Downloads")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { navController.navigate(Routes.SEARCH) }) {
                Text("Search")
            }
        }
    ) { padding ->
        AdSlottedGrid(
            items = faculties,
            modifier = Modifier.padding(padding),
            cardContent = { faculty ->
                Card(
                    onClick = { navController.navigate(Routes.departments(faculty.id)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(faculty.name, modifier = Modifier.padding(16.dp))
                }
            },
            adContent = { NativeAdCard(modifier = Modifier.padding(8.dp)) }
        )
    }
}
