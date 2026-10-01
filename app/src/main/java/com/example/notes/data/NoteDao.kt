package com.example.notes.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import com.example.notes.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun get(id: Long): Flow<NoteEntity?>

    @Upsert
    suspend fun upsert(note: NoteEntity): Long

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun delete(id: Long)
}