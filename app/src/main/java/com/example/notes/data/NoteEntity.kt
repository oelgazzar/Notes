package com.example.notes.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.example.notes.models.Note
import java.time.LocalDate

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,
)

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    body = body,
    dueDate = dueDate?.let { LocalDate.parse(it) },
    isPinned = isPinned
)

fun List<NoteEntity>.toDomain() = map { it.toDomain() }

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    body = body,
    dueDate = dueDate?.toString(),
    isPinned = isPinned
)