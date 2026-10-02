package com.example.notes.ui.home

import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NoteRepository
import com.example.notes.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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

    private var deletedNote: Note? = null

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun setDeletedNote(note: Note?) {
        deletedNote = note
        note?.let {
            viewModelScope.launch {
                _uiEvents.send(UiEvent.ShowSnackBar("Note deleted", note))
            }
        }
    }

    fun undoDeleteNote() {
        deletedNote?.let {
            viewModelScope.launch {
                noteRepository.insert(it.copy(id=0))
                deletedNote = null
            }
        }
    }

    fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            noteRepository.delete(noteIds)
        }
    }

    fun deleteNote(note: Note) {
        if (note == deletedNote) return

        viewModelScope.launch {
            noteRepository.delete(note)
            setDeletedNote(note)
        }
    }
}

sealed interface UiEvent{
    data class ShowSnackBar(val message: String, val deletedNote: Note?) : UiEvent
}