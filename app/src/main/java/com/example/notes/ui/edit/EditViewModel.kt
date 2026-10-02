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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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
    private var savedNoteId: Long? =
        savedStateHandle.toRoute<NavDestination.Edit>().id.takeIf { it != 0L }
    private val isNewNote = savedNoteId == null

    private val _noteDraft = MutableStateFlow(Note())
    val noteDraft = _noteDraft
        .asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private var autoSaveJob: Job? = null

    init {
        viewModelScope.launch {
            getSavedNote()
            autoSaveJob = launch {
                startAutosave()
            }
        }
    }

    private suspend fun getSavedNote() {
        // populate the draft with saved not or create a fresh new one
        savedNoteId?.let {
            _noteDraft.value = noteRepository.get(it).filterNotNull().first()
        }
    }

    private suspend fun startAutosave() {
        _noteDraft
            .debounce(500.milliseconds)
            .collectLatest { currentNoteDraft ->
                if (!currentNoteDraft.isEmpty) {
                    saveNote(currentNoteDraft)
                }
            }
    }

    private suspend fun saveNote(currentNoteDraft: Note) {
        val id = savedNoteId
        if (id == null) {
            savedNoteId = noteRepository.insert(currentNoteDraft)
        } else {
            noteRepository.update(currentNoteDraft.copy(id = id))

        }
    }

    // Called from ui on text input change
    fun updateNote(updatedNoteDraft: Note) {
        _noteDraft.value = updatedNoteDraft
    }


    // Called when leave screen or back button is pressed
    fun saveNoteOrDeleteIfEmpty() {
        autoSaveJob?.cancel()

        viewModelScope.launch {
            val id = savedNoteId
            if (!noteDraft.value.isEmpty) {
                // Not empty -> save
                saveNote(_noteDraft.value)
                _uiEvents.send(UiEvent.NavigateBack(null))
            } else {
                // Empty -> discard and show message if not new
                deleteNote(id)
                _uiEvents.send(UiEvent.NavigateBack(_noteDraft.value.takeIf { !isNewNote }))
            }
        }
    }

    private suspend fun deleteNote(id: Long?) {
        if (id == null) return
        noteRepository.delete(id)
    }

    fun deleteNote() {
        val id = savedNoteId

        viewModelScope.launch {
            if (id != null) {
                deleteNote(id)
            }
            _uiEvents.send(
                UiEvent.NavigateBack(
                    id?.let { _noteDraft.value.copy(id = it) }
                )
            )
        }
    }
}

sealed interface UiEvent {
    data class NavigateBack(val deletedNote: Note?) : UiEvent
}