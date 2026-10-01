package com.example.notes.ui.navigation

import kotlinx.serialization.Serializable

sealed interface NavDestination {
    @Serializable
    object Home: NavDestination
    @Serializable
    data class Edit(val id: Long): NavDestination
}