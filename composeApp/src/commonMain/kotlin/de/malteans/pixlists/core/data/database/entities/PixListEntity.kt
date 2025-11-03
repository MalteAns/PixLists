package de.malteans.pixlists.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PixListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
)
