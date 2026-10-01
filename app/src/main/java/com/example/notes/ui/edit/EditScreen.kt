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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notes.models.Note
import com.example.notes.ui.theme.NotesTheme
import com.example.test.arrow_back
import com.example.test.close
import com.example.test.more_vert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateToHome: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = hiltViewModel()
) {
    val note by viewModel.noteDraft.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect {
            when (it) {
                is UiEvent.NavigateBack -> {
                    navigateToHome(viewModel.broadcastMessage)
                }
            }
        }
    }

    val onLeave = {
        viewModel.saveOrDeleteNote()
    }

    BackHandler {
        onLeave()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        onLeave()
                    },
                        modifier = Modifier
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = arrow_back,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            more_vert,
                            null
                        )
                    }
                }
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