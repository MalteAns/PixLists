package de.malteans.pixlists.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["widgetId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = PixDashboardWidgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["widgetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PixCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["widgetId"]),
        Index(value = ["categoryId"]),
    ]
)
data class PixDashboardWidgetCategoryEntity(
    val widgetId: Long,
    val categoryId: Long,
)
