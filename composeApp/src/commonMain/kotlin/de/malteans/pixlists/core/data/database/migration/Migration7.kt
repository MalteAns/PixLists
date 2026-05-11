package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION6_7: Migration
    get() = object : Migration(6, 7) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE `PixCategoryEntity` ADD COLUMN `enableWeight` INTEGER NOT NULL DEFAULT 0")
            connection.execSQL("ALTER TABLE `PixCategoryEntity` ADD COLUMN `minWeight` INTEGER NOT NULL DEFAULT 0")
            connection.execSQL("ALTER TABLE `PixCategoryEntity` ADD COLUMN `maxWeight` INTEGER NOT NULL DEFAULT 100")
            connection.execSQL("ALTER TABLE `PixCategoryEntity` ADD COLUMN `weightStep` INTEGER NOT NULL DEFAULT 10")
        }
    }
