package com.example.notes.ui.edit

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.room3.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.AndroidJUnitRunner
import com.example.notes.MainActivity
import com.example.notes.data.AppDatabase
import com.example.notes.data.NoteDao
import com.example.notes.data.NoteRepository
import com.example.notes.di.DatabaseModule
import com.example.notes.models.Note
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import jakarta.inject.Inject
import jakarta.inject.Singleton
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EditScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var noteRepository: NoteRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun insertedNote_displayed() = runTest {
        noteRepository.insert(Note(title = "Test title"))

        composeRule.onNodeWithText("Test title").assertIsDisplayed()

        composeRule
            .onNodeWithTag("add_note_fab")
            .performClick()
        composeRule
            .onNodeWithText("Title").performTextInput("Test Title")
        composeRule.onNodeWithText("Note").performTextInput("Test Body")
        composeRule.onNodeWithTag("back_button")
            .performClick()
        composeRule
            .onNodeWithText("Test Title")
            .assertIsDisplayed()
    }

    @Test
    fun emptyNote_discarded() = runTest {
        composeRule
            .onNodeWithTag("add_note_fab")
            .performClick()
        composeRule
            .onNodeWithTag("back_button")
            .performClick()

        val notes = noteRepository.getAll().first()
        assert(notes.isEmpty())
    }

    @Test
    fun nonEmptyNote_saved() = runTest {
        composeRule
            .onNodeWithTag("add_note_fab")
            .performClick()
        composeRule
            .onNodeWithText("Title").performTextInput("Test Title")
        composeRule.onNodeWithText("Note").performTextInput("Test Body")
        composeRule
            .onNodeWithTag("back_button")
            .performClick()

        val notes = noteRepository.getAll().first()
        assertEquals(1, notes.size)
    }

    @Test
    fun typeThenClear_discardNote() = runTest {
        composeRule
            .onNodeWithTag("add_note_fab")
            .performClick()
        composeRule
            .onNodeWithText("Title").performTextInput("Test Title")
        composeRule.onNodeWithText("Note").performTextInput("Test Body")
        composeRule
            .onNodeWithText("Test Title").performTextClearance()
        composeRule.onNodeWithText("Test Body").performTextClearance()
        composeRule
            .onNodeWithTag("back_button")
            .performClick()

        val notes = noteRepository.getAll().first()
        assertEquals(0, notes.size)
    }

    @Test
    fun clearNote_deleted() = runTest {
        noteRepository.insert(Note(title = "Test Title"))
        composeRule
            .onNodeWithText("Test Title")
            .performClick()
        composeRule
            .onNodeWithText("Test Title").performTextClearance()
        composeRule
            .onNodeWithTag("back_button")
            .performClick()

        val notes = noteRepository.getAll().first()
        assertEquals(0, notes.size)
    }

    @Test
    fun editNote_updated() = runTest {
        noteRepository.insert(Note(title = "Test Title"))
        composeRule
            .onNodeWithText("Test Title")
            .performClick()
        composeRule
            .onNodeWithText("Test Title").performTextClearance()
        composeRule
            .onNodeWithText("Title").performTextInput("Updated Title")
        composeRule
            .onNodeWithTag("back_button")
            .performClick()
        composeRule
            .onNodeWithText("Updated Title").assertIsDisplayed()
    }
}

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

// A custom runner to set up the instrumented application class for tests.
class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
