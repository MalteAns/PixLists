package de.malteans.pixlists.core.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.malteans.pixlists.core.data.database.entities.PixCategoryEntity
import de.malteans.pixlists.core.data.database.entities.PixColorEntity
import de.malteans.pixlists.core.data.database.entities.PixEntryEntity
import de.malteans.pixlists.core.data.database.entities.PixListEntity

@Database(
    entities = [PixListEntity::class, PixEntryEntity::class, PixCategoryEntity::class, PixColorEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    CustomTypeConverter::class
)
@ConstructedBy(PixDatabaseConstructor::class)
abstract class PixDatabase : RoomDatabase() {

    abstract val pixDao: PixDao

    companion object {
        const val DB_NAME = "pix.db"
    }
}