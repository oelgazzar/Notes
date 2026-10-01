package com.example.notes.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.notes.data.NoteRepository
import com.example.notes.models.Note
import com.example.notes.ui.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class EditViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var savedNoteId = savedStateHandle.toRoute<NavDestination.Edit>().id
    private val newNote = savedNoteId == 0L

    private val _noteDraft = MutableStateFlow(Note())
    val noteDraft = _noteDraft
        .asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    var broadcastMessage: String? = null
        private set

    init {
        getSavedNote()
    }

    private fun getSavedNote() {
        viewModelScope.launch {
            // populate the draft with saved not or create a fresh new one
            _noteDraft.value = noteRepository.get(savedNoteId).map { it ?: Note() }.first()
            startAutosave()
        }
    }

    fun updateNote(updatedNoteDraft: Note) {
        _noteDraft.value = updatedNoteDraft
    }

    private fun startAutosave() {
        viewModelScope.launch {
            _noteDraft
                .debounce(500.milliseconds)
                .collectLatest { currentNoteDraft ->
                    if (!currentNoteDraft.isEmpty) {
                        saveNote(currentNoteDraft)
                    }
                }
        }
    }

    /* Called when leave screen or back button is pressed */
    fun saveOrDeleteNote() {
        viewModelScope.launch {
            if (noteDraft.value.isEmpty) {
                deleteNote(savedNoteId)
            } else {
                saveNote(_noteDraft.value)
            }
            _uiEvents.send(UiEvent.NavigateBack)
        }
    }

    private suspend fun deleteNote(savedNoteId: Long) {
        if (newNote) return

        noteRepository.delete(savedNoteId)
        broadcastMessage = "Empty note discarded"
    }

    private suspend fun saveNote(currentNoteDraft: Note) {
        when {
            savedNoteId == 0L -> {
                savedNoteId = noteRepository.insert(currentNoteDraft)
            }

            else -> noteRepository.update(currentNoteDraft.copy(id = savedNoteId))
        }
    }
}

sealed interface UiEvent {
//    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
}