package de.malteans.pixlists.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import de.malteans.pixlists.data.database.entities.PixCategoryEntity
import de.malteans.pixlists.data.database.entities.PixColorEntity
import de.malteans.pixlists.data.database.entities.PixEntryEntity
import de.malteans.pixlists.data.database.entities.PixListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface PixDao {

    // PixList Operations ------------------------------------------------------
    @Upsert
    suspend fun upsertList(list: PixListEntity): Long

    @Query("DELETE FROM pixlistentity WHERE id = :listId")
    suspend fun deleteListById(listId: Long)

    @Query("SELECT * FROM pixlistentity WHERE id = :listId")
    fun getList(listId: Long): Flow<PixListEntity?>

    @Query("SELECT * FROM pixlistentity")
    fun getAllLists(): Flow<List<PixListEntity>>

    @Query("UPDATE pixlistentity SET name = :newName WHERE id = :listId")
    suspend fun renameList(listId: Long, newName: String)

    // PixEntry Operations -----------------------------------------------------
    @Upsert
    suspend fun upsertEntry(entry: PixEntryEntity): Long

    @Query("DELETE FROM pixentryentity WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Long)

    @Query("DELETE FROM pixentryentity WHERE listId = :listId AND date = :date")
    suspend fun deleteEntryByListIdAndDate(listId: Long, date: LocalDate)

    @Query("SELECT * FROM pixentryentity WHERE id = :entryId")
    fun getEntry(entryId: Long): Flow<PixEntryEntity>

    @Query("SELECT * FROM pixentryentity")
    suspend fun getAllEntriesWithoutUpdate(): List<PixEntryEntity>

    @Query("SELECT * FROM pixentryentity WHERE listId = :listId AND date = :date")
    suspend fun getEntriesWithoutUpdate(listId: Long, date: LocalDate): List<PixEntryEntity>

    @Query("SELECT * FROM pixentryentity WHERE listId = :listId")
    fun getEntriesForList(listId: Long): Flow<List<PixEntryEntity>>

    // PixCategory Operations --------------------------------------------------
    @Upsert
    suspend fun upsertCategory(category: PixCategoryEntity): Long

    @Query("DELETE FROM pixcategoryentity WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("SELECT * FROM pixcategoryentity WHERE id = :categoryId")
    fun getCategory(categoryId: Long): Flow<PixCategoryEntity>

    @Query("SELECT * FROM pixcategoryentity WHERE listId = :listId")
    fun getCategoriesForList(listId: Long): Flow<List<PixCategoryEntity>>

    @Query("SELECT * FROM pixcategoryentity")
    fun getAllCategories(): Flow<List<PixCategoryEntity>>

    @Query("SELECT * FROM pixcategoryentity")
    suspend fun getAllCategoriesWithoutUpdate(): List<PixCategoryEntity>

    @Query("SELECT COUNT(*) FROM pixcategoryentity WHERE listId = :listId")
    suspend fun getCategoryCountForList(listId: Long): Int

    @Query("UPDATE pixcategoryentity SET name = :newName WHERE id = :categoryId")
    suspend fun renameCategory(categoryId: Long, newName: String)

    @Query("UPDATE pixcategoryentity SET colorId = :newColorId WHERE id = :categoryId")
    suspend fun changeCategoryColor(categoryId: Long, newColorId: Long)

    @Query("UPDATE pixcategoryentity SET orderIndex = :newOrderIndex WHERE id = :categoryId")
    suspend fun changeCategoryOrderIndex(categoryId: Long, newOrderIndex: Int)

    // PixColor Operations -----------------------------------------------------
    @Upsert
    suspend fun upsertColor(color: PixColorEntity): Long

    @Query("DELETE FROM pixcolorentity WHERE id = :colorId")
    suspend fun deleteColorById(colorId: Long)

    @Query("DELETE FROM pixcolorentity WHERE id NOT IN (SELECT DISTINCT colorId FROM pixcategoryentity)")
    suspend fun deleteUnusedColors(): Int

    @Query("SELECT * FROM pixcolorentity WHERE id = :colorId")
    fun getColor(colorId: Long): Flow<PixColorEntity>

    @Query("SELECT * FROM pixcolorentity")
    fun getAllColors(): Flow<List<PixColorEntity>>

    @Query("SELECT * FROM pixcolorentity")
    suspend fun getAllColorsWithoutUpdate(): List<PixColorEntity>

    @Query("UPDATE pixcolorentity SET name = :newName WHERE id = :colorId")
    suspend fun renameColor(colorId: Long, newName: String)

    @Query("UPDATE pixcolorentity " +
            "SET red = :newRed, green = :newGreen, blue = :newBlue WHERE id = :colorId")
    suspend fun changeColor(colorId: Long, newRed: Float, newGreen: Float, newBlue: Float)
}
