package com.example.notes.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.note_alt

@Preview(name = "Empty List")
@Composable
fun EmptyListScreen(modifier: Modifier = Modifier.Companion) {
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
        Text(
            "No notes. Enjoy!",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}