package com.example.notes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.edit.EditScreen
import com.example.notes.ui.home.HomeScreen

@Composable
fun NavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController,
        NavDestination.Home
    ) {
        composable<NavDestination.Home>{ backStackEntry ->
            val message = backStackEntry.savedStateHandle.get<String>("message")
            val deletedNote = backStackEntry.savedStateHandle.get<String>("deletedNote")
            HomeScreen(
                message = message,
                deletedNoteSerialized = deletedNote,
                onNavigateToNote = { navController.navigate(NavDestination.Edit(it)) }
            )
        }

        composable<NavDestination.Edit> {
            EditScreen(
                navigateToHome = { message, deletedNote ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("message", message)
                    navController.previousBackStackEntry?.savedStateHandle?.set("deletedNote", deletedNote)
                    navController.navigateUp()
                }
            )
        }
    }
}