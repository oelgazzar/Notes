package com.example.notes.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notes.models.Note
import com.example.notes.ui.components.ConfirmDeleteDialog
import com.example.notes.ui.theme.NotesTheme
import com.example.test.add
import com.example.test.close
import com.example.test.delete
import com.example.test.keep
import com.example.test.note_alt
import com.example.test.search
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    deletedNote: Note?,
    onNavigateToNote: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val selectedItemIds = rememberSaveable { mutableStateSetOf<Long>() }
    val inSelectionMode by remember { derivedStateOf { selectedItemIds.isNotEmpty() } }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.updateDeletedNote(deletedNote)

        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.NoteDeleted -> {
                    val result = snackbarHostState.showSnackbar(
                        event.message,
                        actionLabel = if (event.undo) "Undo" else null,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDeleteNote()
                        listState.animateScrollToItem(notes.lastIndex.coerceAtLeast(0))
                    }

                    viewModel.onMessageShown()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (inSelectionMode) {
                        Text(
                            "${selectedItemIds.size} notes selected",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            "Notes",
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                },
                navigationIcon = {
                    if (inSelectionMode) {
                        IconButton(onClick = { selectedItemIds.clear() }) {
                            Icon(
                                close,
                                null
                            )
                        }
                    }
                },
                actions = {
                    if (inSelectionMode) {
                        IconButton(onClick = {
                            showConfirmDeleteDialog = true
                        }) {
                            Icon(
                                delete,
                                null
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToNote(Note().id) },
                shape = CircleShape,
                modifier = Modifier
                    .testTag("add_note_fab")
            ) {
                Icon(
                    add,
                    null,
                    modifier = Modifier
                        .size(36.dp)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    shape = CircleShape
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            if (notes.isEmpty()) {
                EmptyListScreen()
            } else {
                NoteList(
                    notes,
                    onNoteClicked = onNavigateToNote,
                    selectedItemIds = selectedItemIds,
                    onSelectItem = { id, selected ->
                        if (selected) selectedItemIds.add(id) else selectedItemIds.remove(
                            id
                        )
                    },
                    inSelectionMode = inSelectionMode,
                    onDismissItem = viewModel::deleteNote,
                    state = listState,
                )
            }
        }
    }

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                viewModel.deleteNotes(selectedItemIds.toList())
                selectedItemIds.clear()
            },
            onDismiss = { showConfirmDeleteDialog = false }
        )
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    onNoteClicked: (Long) -> Unit,
    selectedItemIds: Set<Long>,
    onSelectItem: (Long, Boolean) -> Unit,
    inSelectionMode: Boolean,
    onDismissItem: (Note) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    val sortedNotes = notes.sortedByDescending { it.isPinned }

    LazyColumn(
        state = state,
        modifier = modifier
    ) {
        items(sortedNotes, key = { it.id }) { note ->
            val isItemSelected by remember { derivedStateOf { selectedItemIds.contains(note.id) } }
            NoteItem(
                note,
                onClicked = { onNoteClicked(note.id) },
                inSelectionMode = inSelectionMode,
                onSelectItem = { selected -> onSelectItem(note.id, selected) },
                selected = isItemSelected,
                onDismiss = { onDismissItem(note) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .animateItem()
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClicked: () -> Unit,
    inSelectionMode: Boolean,
    onSelectItem: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColors = when {
        note.isPinned ->
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )

        else -> {
            CardDefaults.cardColors()
        }
    }

    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    var alpha by remember { mutableFloatStateOf(0f) }
    val dismissState = rememberSwipeToDismissBoxState()
    val coroutineScope = rememberCoroutineScope()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        onDismiss = { value ->
            if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                onDismiss()
            } else {
                coroutineScope.launch {
                    dismissState.reset()
                }
            }
        },
        modifier = modifier
    ) {
        Card(
            colors = cardColors,
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, borderColor, MaterialTheme.shapes.medium)
                .combinedClickable(
                    onClick = {
                        if (inSelectionMode) {
                            onSelectItem(!selected)
                        } else {
                            onClicked()
                        }
                    },
                    onLongClick = { onSelectItem(!selected) }
                )
                .graphicsLayer {
                    try {
                        alpha = (1 - dismissState.requireOffset() / size.width).coerceIn(0.2f, 1f)
                    } catch (_: IllegalStateException) {
                    }
                }
                .alpha(alpha)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.isPinned) {
                        Icon(
                            keep,
                            null,
                            modifier = Modifier
                                .rotate(45f)
                                .size(30.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    Text(
                        note.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    note.body,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                note.dueDate?.let {
                    Text(
                        note.dueDate.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(name = "Empty List")
@Composable
fun EmptyListScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Icon(
            note_alt,
            null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(128.dp)
        )
        Text("No notes. Enjoy!",
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    val (query, setQuery) = rememberSaveable { mutableStateOf("") }

    TextField(
        value = query,
        onValueChange = setQuery,
        placeholder = {
            Text("Search notes..")
        },
        leadingIcon = {
            Icon(
                search,
                null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { setQuery("") }) {
                    Icon(
                        close,
                        null
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        modifier = modifier
    )

}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SearchBarPrev() {
    NotesTheme {
        Surface(

        ) {
            SearchBar(
                modifier = Modifier
                    .padding(16.dp)
            )

        }
    }
}

//@Preview(uiMode = UI_MODE_NIGHT_YES)
//@Preview
//@Composable
//private fun NoteListPrev() {
//    NotesTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            NoteList(
//                listOf(
//                    Note(
//                        1,
//                        "Shopping List",
//                        "Buy milk, eggs, bread, and coffee.",
//                        dueDate = LocalDate.now(),
//                        isPinned = true
//                    ),
//                    Note(
//                        2,
//                        "Project Ideas",
//                        "Build a simple expense tracker using Jetpack Compose."
//                    ),
//                    Note(
//                        3,
//                        "Meeting Notes",
//                        "Discuss the new project requirements and deadlines."
//                    ),
//                    Note(
//                        4,
//                        "Workout Plan",
//                        "Monday: chest and triceps. Wednesday: back and biceps.",
//                        dueDate = LocalDate.now().plusDays(2)
//                    ),
//                    Note(
//                        5,
//                        "Book Recommendation",
//                        "Read Clean Code to improve programming practices.",
//                        isPinned = true
//                    ),
//                    Note(
//                        6,
//                        "Weekend Plans",
//                        "Finish the Android project and play some games."
//                    ),
//                    Note(
//                        7,
//                        "Important Reminder",
//                        "Backup the database before making major changes."
//                    ),
//                    Note(
//                        8,
//                        "Learning Kotlin",
//                        "Review coroutines, Flow, StateFlow, and sealed classes."
//                    ),
//                    Note(
//                        9,
//                        "App Design",
//                        "Create a simple home screen with a top bar and bottom navigation."
//                    ),
//                    Note(
//                        10,
//                        "Ideas",
//                        "Add search, filtering, and sorting to the notes app."
//                    )
//                ),
//                onNoteClicked = {},
//                modifier = Modifier
//                    .padding(16.dp)
//            )
//        }
//    }
//}

//@Preview
//@Composable
//private fun NoteItemPrev() {
//    NoteItem(
//        note = Note(
//            id = 1,
//            title = "Shopping List",
//            body = "Buy milk, eggs, bread, and coffee.",
//        ),
//        onClicked = {}
//    )
//}