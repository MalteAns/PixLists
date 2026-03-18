package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION3_4: Migration
    get() = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            // 1) Create new table for PixDashboardWidgetEntity
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `PixDashboardWidgetEntity` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `listId` INTEGER NOT NULL,
                    `type` TEXT NOT NULL,
                    FOREIGN KEY(`listId`) REFERENCES `PixListEntity`(`id`) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_PixDashboardWidgetEntity_listId` ON `PixDashboardWidgetEntity` (`listId`)")
            // 2) Create new table for PixDashboardWidgetCategoryEntity
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `PixDashboardWidgetCategoryEntity` (
                    `widgetId` INTEGER NOT NULL,
                    `categoryId` INTEGER NOT NULL,
                    PRIMARY KEY(`widgetId`, `categoryId`),
                    FOREIGN KEY(`widgetId`) REFERENCES `PixDashboardWidgetEntity`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`categoryId`) REFERENCES `PixCategoryEntity`(`id`) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_PixDashboardWidgetCategoryEntity_widgetId` ON `PixDashboardWidgetCategoryEntity` (`widgetId`)")
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_PixDashboardWidgetCategoryEntity_categoryId` ON `PixDashboardWidgetCategoryEntity` (`categoryId`)")
        }
    }