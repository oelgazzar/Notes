package com.example.notes.di

import com.example.notes.data.LocalNoteRepository
import com.example.notes.data.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindNoteRepository(impl: LocalNoteRepository): NoteRepository
}