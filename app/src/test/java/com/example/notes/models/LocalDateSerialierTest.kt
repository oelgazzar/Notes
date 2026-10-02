package com.example.notes.models

import android.util.Log
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalDate

class LocalDateSerializerTest {
    @Test
    fun serializeNote_properJsonFormat() {
        val note = Note(
            title = "Test Title",
            body = "Test Body",
            dueDate = LocalDate.now()
        )

        val r = Json.encodeToString(Note.serializer(), note)
        assertEquals("{\"title\":\"Test Title\",\"body\":\"Test Body\",\"dueDate\":\"2026-10-02\"}", r)
    }
}