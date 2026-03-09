package de.malteans.pixlists.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.malteans.pixlists.dashboard.domain.WidgetType

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PixListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["listId"]),
    ]
)
data class PixDashboardWidgetEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val listId: Long,
    val type: WidgetType,
)