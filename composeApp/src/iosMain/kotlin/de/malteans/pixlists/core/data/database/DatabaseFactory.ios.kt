package de.malteans.pixlists.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.pixlists.core.data.database.migration.MIGRATION1_2
import de.malteans.pixlists.core.data.database.migration.MIGRATION2_3
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<PixDatabase> {
        val dbFilePath = documentDirectory() + "/${PixDatabase.DB_NAME}"
        return Room.databaseBuilder<PixDatabase>(dbFilePath)
            .addMigrations(
                PixDatabase.MIGRATION1_2,
                PixDatabase.MIGRATION2_3,
            )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}