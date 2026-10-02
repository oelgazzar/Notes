package com.example.notes.di

import android.content.Context
import androidx.room3.Room
import com.example.notes.data.AppDatabase
import com.example.notes.data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import jakarta.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
@Module
object TestDatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            applicationContext,
            AppDatabase::class.java,
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao {
        return db.noteDao()
    }
}