package com.example.notes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.serialization.encodeToSavedState
import com.example.notes.models.Note
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
        composable<NavDestination.Home> { backStackEntry ->
            val deletedNote =
                Note.fromString(backStackEntry.savedStateHandle.get<String>("deletedNote"))
            HomeScreen(
                deletedNote = deletedNote,
                onNavigateToNote = { navController.navigate(NavDestination.Edit(it)) }
            )
        }

        composable<NavDestination.Edit> {
            EditScreen(
                navigateToHome = { deletedNote ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "deletedNote",
                        deletedNote.toString()
                    )
                    navController.navigateUp()
                }
            )
        }
    }
}