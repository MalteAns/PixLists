package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION4_5: Migration
    get() = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE `PixDashboardWidgetEntity` ADD COLUMN `orderIndex` INTEGER NOT NULL DEFAULT 0")
        }
    }

