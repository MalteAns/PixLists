package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION5_6: Migration
    get() = object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE `PixEntryEntity` ADD COLUMN `weight` INTEGER DEFAULT NULL")
        }
    }

