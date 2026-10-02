package com.example.notes.ui.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notes.models.Note
import com.example.notes.ui.components.ConfirmDeleteDialog
import com.example.notes.ui.theme.NotesTheme
import com.example.test.close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateToHome: (Note?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = hiltViewModel()
) {
    val note by viewModel.noteDraft.collectAsStateWithLifecycle()
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> {
                    navigateToHome(event.deletedNote)
                }
            }
        }
    }

    BackHandler {
        viewModel.saveNoteOrDeleteIfEmpty()
    }

    Scaffold(
        topBar = {
            EditTopAppBar(
                onNavigateBack = viewModel::saveNoteOrDeleteIfEmpty,
                onDeleteClicked = { showConfirmDeleteDialog = true }
            )
        }
    ) { innerPadding ->
        EditForm(
            note = note,
            onNoteChanged = viewModel::updateNote,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        )
    }

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = { viewModel.deleteNote() },
            onDismiss = { showConfirmDeleteDialog = false }
        )
    }
}

@Composable
fun EditForm(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        NoteEditField(
            text = note.title,
            onTextChanged = { onNoteChanged(note.copy(title = it)) },
            placeholder = "Title",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            hasClearIcon = true,
            isSingleLine = true,
            modifier = Modifier
                .padding(bottom = 24.dp)
        )

        NoteEditField(
            text = note.body,
            onTextChanged = { onNoteChanged(note.copy(body = it)) },
            placeholder = "Note",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            hasClearIcon = false,
            isSingleLine = false,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun NoteEditField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true,
    hasClearIcon: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            textStyle = style.copy(color = color),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = isSingleLine,
            modifier = Modifier
                .weight(1f)
        ) { innerTextField ->
            Box {
                text.ifEmpty {
                    Text(
                        placeholder,
                        style = style.copy(color = Color.Gray)
                    )
                }
                innerTextField()
            }
        }

        if (text.isNotEmpty() && hasClearIcon) {
            IconButton(onClick = { onTextChanged("") }) {
                Icon(
                    close,
                    null
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditFormPrev() {
    NotesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EditForm(
                note = Note(),
                onNoteChanged = {},
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
            )
        }
    }
}