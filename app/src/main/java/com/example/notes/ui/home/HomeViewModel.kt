package com.example.notes.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NoteRepository
import com.example.notes.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
) : ViewModel() {
    val notes = noteRepository.getAll()
        .onEach { Log.d("HomeViewModel", "List updated") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val deletedNote = MutableStateFlow<Note?>(null)
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            deletedNote.filterNotNull()
                .collect {
                    val emptyNote = it.isEmpty
                    _uiEvents.send(
                        UiEvent.NoteDeleted(
                            if (emptyNote) "Empty note discarded" else "Note deleted",
                            undo = !emptyNote
                        )
                    )
                }
        }
    }

    fun onMessageShown() {
        deletedNote.value = null
    }

    fun undoDeleteNote() {
        deletedNote.value?.let {
            viewModelScope.launch {
                noteRepository.insert(it.copy(id = 0))
                deletedNote.value = null
            }
        }
    }

    fun updateDeletedNote(note: Note?) {
        deletedNote.value = note
    }

    fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            noteRepository.delete(noteIds)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.delete(note)
            updateDeletedNote(note)
        }
    }
}

sealed interface UiEvent {
    data class NoteDeleted(val message: String, val undo: Boolean = false) : UiEvent
}