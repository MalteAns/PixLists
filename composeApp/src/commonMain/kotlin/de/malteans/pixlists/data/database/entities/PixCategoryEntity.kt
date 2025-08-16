package de.malteans.pixlists.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PixListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PixColorEntity::class,
            parentColumns = ["id"],
            childColumns = ["colorId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["listId"]),
        Index(value = ["colorId"])
    ]
)
data class PixCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val listId: Long,
    val colorId: Long?,
    val name: String = "",
    val orderIndex: Int,
)
