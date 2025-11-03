package de.malteans.pixlists.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.pixlists.core.data.database.PixDatabase

val PixDatabase.Companion.MIGRATION1_2: Migration
    get() = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            // 1) Create new table with the updated schema + foreign keys
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `PixEntryEntity_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `listId` INTEGER NOT NULL,
                    `categoryId` INTEGER NOT NULL,
                    `date` INTEGER NOT NULL,
                    FOREIGN KEY(`listId`) REFERENCES `PixListEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`categoryId`) REFERENCES `PixCategoryEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )

            // 2) Copy data from old table, converting "MM-dd" (assume year 2025) to epochDay
            connection.execSQL(
                """
                INSERT INTO `PixEntryEntity_new` (id, listId, categoryId, date)
                SELECT
                    id,
                    listId,
                    categoryId,
                    CAST(
                        julianday('2025-' || substr(date, 1, 2) || '-' || substr(date, 4, 2))
                        - julianday('1970-01-01')
                        AS INTEGER
                    ) AS epoch_day
                FROM `PixEntryEntity`
                WHERE date IS NOT NULL
                  AND length(date) = 5
                  AND substr(date, 3, 1) = '-'
                  AND julianday('2025-' || substr(date, 1, 2) || '-' || substr(date, 4, 2)) IS NOT NULL
                """.trimIndent()
            )

            // 3) Replace old table with the new one
            connection.execSQL("""DROP TABLE `PixEntryEntity`""")
            connection.execSQL("""ALTER TABLE `PixEntryEntity_new` RENAME TO `PixEntryEntity`""")

            // 4) Recreate indices that Room expects
            connection.execSQL(
                """CREATE INDEX IF NOT EXISTS `index_PixEntryEntity_listId` ON `PixEntryEntity`(`listId`)"""
            )
            connection.execSQL(
                """CREATE INDEX IF NOT EXISTS `index_PixEntryEntity_categoryId` ON `PixEntryEntity`(`categoryId`)"""
            )

            // 5) Optional: keep AUTOINCREMENT sequence tidy
            connection.execSQL(
                """
                UPDATE sqlite_sequence
                   SET seq = (SELECT IFNULL(MAX(id), 0) FROM `PixEntryEntity`)
                 WHERE name = 'PixEntryEntity'
                """.trimIndent()
            )
        }
    }

