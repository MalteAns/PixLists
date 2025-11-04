package de.malteans.pixlists.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity
data class PixListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
    val years: String = Json.encodeToString(listOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)),
)
