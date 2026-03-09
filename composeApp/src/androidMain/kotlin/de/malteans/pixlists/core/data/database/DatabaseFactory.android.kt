package de.malteans.pixlists.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.pixlists.core.data.database.migration.MIGRATION1_2
import de.malteans.pixlists.core.data.database.migration.MIGRATION2_3
import de.malteans.pixlists.core.data.database.migration.MIGRATION3_4

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<PixDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(PixDatabase.DB_NAME)
        return Room.databaseBuilder<PixDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .addMigrations(
                PixDatabase.MIGRATION1_2,
                PixDatabase.MIGRATION2_3,
                PixDatabase.MIGRATION3_4,
            )
    }
}
