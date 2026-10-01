package com.example.notes.models

import java.time.LocalDate

data class Note(
    val id: Long = 0,
    val title: String = "",
    val body: String = "",
    val dueDate: LocalDate? = null,
    val isPinned: Boolean = false
) {
    val isEmpty
        get() = title.isBlank() && body.isBlank()
}
