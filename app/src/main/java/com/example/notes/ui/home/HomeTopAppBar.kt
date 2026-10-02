package com.example.notes.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.test.close
import com.example.test.delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    inSelectionMode: Boolean,
    selectedItemIds: MutableSet<Long>,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        delete,
                        null
                    )
                }
            }
        }
    )
}