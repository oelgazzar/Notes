package com.example.notes.data

import com.example.notes.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalNoteRepository @Inject constructor(
    private val noteDao: NoteDao
): NoteRepository {
    override fun getAll(): Flow<List<Note>> {
        return noteDao.getAll().map { it.toDomain() }
    }

    override fun get(id: Long): Flow<Note?> = noteDao.get(id).map { it?.toDomain() }

    override suspend fun upsert(note: Note) = noteDao.upsert(note.toEntity())

    override suspend fun insert(note: Note) = noteDao.insert(note.toEntity())

    override suspend fun update(note: Note) = noteDao.update(note.toEntity())

    override suspend fun delete(note: Note) = noteDao.delete(note.toEntity())

    override suspend fun delete(id: Long) = noteDao.delete(id)
}