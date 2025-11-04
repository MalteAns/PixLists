package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION2_3: Migration
    get() = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            // 1) Create new table with the updated schema + foreign keys
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `PixListEntity_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `years` TEXT NOT NULL
                )
                """.trimIndent()
            )

            // 2) Copy data with hardcoded year 2025 into new table
            connection.execSQL(
                """
                INSERT INTO `PixListEntity_new` (id, name, years)
                SELECT
                    id,
                    name,
                    '[2025]' AS years
                FROM `PixListEntity`
                """.trimIndent()
            )

            // 3) Replace old table with the new one
            connection.execSQL("""DROP TABLE `PixListEntity`""")
            connection.execSQL("""ALTER TABLE `PixListEntity_new` RENAME TO `PixListEntity`""")

            // 4) Optional: keep AUTOINCREMENT sequence tidy
            connection.execSQL(
                """
                UPDATE sqlite_sequence
                   SET seq = (SELECT IFNULL(MAX(id), 0) FROM `PixListEntity`)
                 WHERE name = 'PixListEntity'
                """.trimIndent()
            )
        }
    }

