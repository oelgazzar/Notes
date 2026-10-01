package com.example.notes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notes.ui.edit.EditScreen
import com.example.notes.ui.home.HomeScreen
import com.example.notes.ui.navigation.NavHost

@Composable
fun NotesApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost()
    }
}