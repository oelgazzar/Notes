package com.example.notes.ui.navigation

import android.util.Log
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
            Log.d("NavHost", message?:"")
            HomeScreen(
                message = message,
                onNavigateToNote = { navController.navigate(NavDestination.Edit(it)) }
            )
        }

        composable<NavDestination.Edit> {
            EditScreen(
                navigateToHome = { message ->
                    Log.d("EditComposable", message?:"")
                    navController.previousBackStackEntry?.savedStateHandle?.set("message", message)
                    navController.navigateUp()
                }
            )
        }
    }
}