package com.example.notes.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "notes-db"
    }
}