package com.example.notes.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notes.ui.theme.NotesTheme
import com.example.test.close
import com.example.test.search

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