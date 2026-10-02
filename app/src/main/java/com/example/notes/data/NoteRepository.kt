package com.example.notes.data

import androidx.room3.Delete
import androidx.room3.Upsert
import com.example.notes.models.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAll(): Flow<List<Note>>

    fun get(id: Long): Flow<Note?>

    suspend fun upsert(note: Note): Long

    suspend fun insert(note: Note): Long

    suspend fun update(note: Note)

    suspend fun delete(note: Note)

    suspend fun delete(id: Long)

    suspend fun delete(ids: List<Long>)
}