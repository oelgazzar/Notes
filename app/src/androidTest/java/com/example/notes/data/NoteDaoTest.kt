package com.example.notes.data

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var noteDao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getAll_returnsInsertedNotes() = runTest {
        val note1 = NoteEntity(
            title = "Note 1",
            body = "Body 1"
        )

        val note2 = NoteEntity(
            title = "Note 2",
            body = "Body 2"
        )

        noteDao.insert(note1)
        noteDao.insert(note2)

        val result = noteDao.getAll().first()

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Note 1" })
        assertTrue(result.any { it.title == "Note 2" })
    }

    @Test
    fun getAll_whenDatabaseEmpty_returnsEmptyList() = runTest {
        val result = noteDao.getAll().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun get_existingId_returnsNote() = runTest {
        val id = noteDao.insert(
            NoteEntity(
                title = "Test",
                body = "Hello"
            )
        )

        val result = noteDao.get(id).first()

        assertNotNull(result)
        assertEquals(id, result!!.id)
        assertEquals("Test", result.title)
        assertEquals("Hello", result.body)
    }

    @Test
    fun get_nonExistingId_returnsNull() = runTest {
        val result = noteDao.get(999L).first()

        assertNull(result)
    }

    @Test
    fun insert_returnsGeneratedId() = runTest {
        val note = NoteEntity(
            title = "Test",
            body = "Hello"
        )

        val id = noteDao.insert(note)

        assertTrue(id > 0)
    }

    @Test
    fun insert_persistsNote() = runTest {
        val note = NoteEntity(
            title = "Test",
            body = "Hello"
        )

        val id = noteDao.insert(note)

        val result = noteDao.get(id).first()

        assertEquals(note.title, result!!.title)
        assertEquals(note.body, result.body)
    }

    @Test
    fun update_changesExistingNote() = runTest {
        val id = noteDao.insert(
            NoteEntity(
                title = "Old title",
                body = "Old body"
            )
        )

        val updated = NoteEntity(
            id = id,
            title = "New title",
            body = "New body"
        )

        noteDao.update(updated)

        val result = noteDao.get(id).first()

        assertEquals("New title", result!!.title)
        assertEquals("New body", result.body)
    }

    @Test
    fun update_nonExistingNote_doesNotInsert() = runTest {
        val note = NoteEntity(
            id = 999L,
            title = "Test",
            body = "Hello"
        )

        noteDao.update(note)

        val result = noteDao.getAll().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteEntity_removesNote() = runTest {
        val id = noteDao.insert(
            NoteEntity(
                title = "Test",
                body = "Hello"
            )
        )

        val note = noteDao.get(id).first()!!

        noteDao.delete(note)

        assertNull(noteDao.get(id).first())
    }

    @Test
    fun deleteById_removesNote() = runTest {
        val id = noteDao.insert(
            NoteEntity(
                title = "Test",
                body = "Hello"
            )
        )

        noteDao.delete(id)

        assertNull(noteDao.get(id).first())
    }

    @Test
    fun deleteById_nonExistingId_doesNothing() = runTest {
        noteDao.delete(999L)

        assertTrue(noteDao.getAll().first().isEmpty())
    }

    @Test
    fun getAll_emitsWhenNoteInserted() = runTest {
        val flow = noteDao.getAll()

        assertTrue(flow.first().isEmpty())

        noteDao.insert(
            NoteEntity(
                title = "Test",
                body = "Hello"
            )
        )

        val result = flow.first { it.isNotEmpty() }

        assertEquals(1, result.size)
        assertEquals("Test", result[0].title)
    }

    @Test
    fun get_emitsWhenNoteUpdated() = runTest {
        val id = noteDao.insert(
            NoteEntity(
                title = "Old",
                body = "Body"
            )
        )

        val flow = noteDao.get(id)

        assertEquals("Old", flow.first()!!.title)

        noteDao.update(
            NoteEntity(
                id = id,
                title = "New",
                body = "Body"
            )
        )

        val result = flow.first { it!!.title == "New" }

        assertEquals("New", result!!.title)
    }
}