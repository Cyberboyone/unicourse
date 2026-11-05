package com.cyberboyone.lecturenotes.ui.screens.department

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
import com.cyberboyone.lecturenotes.ui.viewmodel.DepartmentListViewModel

@Composable
fun DepartmentListScreen(navController: NavHostController, facultyId: String) {
    val container = LocalAppContainer.current
    val viewModel: DepartmentListViewModel =
        viewModel(factory = AppViewModelFactory(container, arg = facultyId))
    val departments by viewModel.departments.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Departments") }) }) { padding ->
        AdSlottedGrid(
            items = departments,
            modifier = Modifier.padding(padding),
            cardContent = { dept ->
                Card(
                    onClick = { navController.navigate(Routes.courses(dept.id)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(dept.name, modifier = Modifier.padding(16.dp))
                }
            },
            adContent = { NativeAdCard(modifier = Modifier.padding(8.dp)) }
        )
    }
}
