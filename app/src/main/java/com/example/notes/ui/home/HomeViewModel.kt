package com.example.notes.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NoteRepository
import com.example.notes.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    val notes = noteRepository.getAll()
        .onEach { Log.d("HomeViewModel", "List updated") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        Log.d("HomeViewModel", "HomeViewModel init")
    }

    fun updateNote() {
        val  r = (1..10).random()
        val s = (1..5).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ".random() }.joinToString("")
        val note = Note(id = r.toLong(), title = s)
        viewModelScope.launch {
            noteRepository.upsert(note)
        }
    }
}