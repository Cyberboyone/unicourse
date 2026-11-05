package com.cyberboyone.lecturenotes.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cyberboyone.lecturenotes.di.AppViewModelFactory
import com.cyberboyone.lecturenotes.di.LocalAppContainer
import com.cyberboyone.lecturenotes.ui.components.AdSlottedGrid
import com.cyberboyone.lecturenotes.ui.components.NativeAdCard
import com.cyberboyone.lecturenotes.ui.navigation.Routes
import com.cyberboyone.lecturenotes.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(navController: NavHostController) {
    val container = LocalAppContainer.current
    val viewModel: SearchViewModel = viewModel(factory = AppViewModelFactory(container))
    val results by viewModel.results.collectAsState()
    var query by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.onQueryChanged(it)
            },
            label = { Text("Course code or name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        AdSlottedGrid(
            items = results,
            cardContent = { course ->
                Card(
                    onClick = { navController.navigate(Routes.courseDetail(course.id)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(course.title, modifier = Modifier.padding(16.dp))
                }
            },
            adContent = { NativeAdCard(modifier = Modifier.padding(8.dp)) }
        )
    }
}
