package com.example.notes.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDate

@Serializable
data class Note(
    val id: Long = 0,
    val title: String = "",
    val body: String = "",
    @Serializable(with = LocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    val isPinned: Boolean = false
) {
    val isEmpty
        get() = title.isBlank() && body.isBlank()

    override fun toString(): String {
        return Json.encodeToString(this)
    }

    companion object {
        fun fromString(string: String?): Note? {
            return string?.let { Json.decodeFromString(it) }
        }
    }
}

object LocalDateSerializer: KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDate
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }

}