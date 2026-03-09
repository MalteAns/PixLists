package de.malteans.pixlists.core.domain

import de.malteans.pixlists.dashboard.domain.PixDashboardWidget
import de.malteans.pixlists.dashboard.domain.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.JsonElement

interface PixRepository {

    // List Operations --------------------------------------------------
    suspend fun createList(name: String): Long

    suspend fun deleteListById(listId: Long)

    suspend fun renameList(listId: Long, newName: String)

    fun getAllPixListsWithoutData(): Flow<List<PixList>>

    suspend fun addYearToList(listId: Long, year: Int)

    /** PixList with all it's info */
    fun getCurrentPixList(listId: Long): Flow<PixList?>

    // Category Operations ----------------------------------------------
    suspend fun createCategory(listId: Long, colorId: Long, name: String): Long

    suspend fun deleteCategoryById(categoryId: Long)

    suspend fun renameCategory(categoryId: Long, newName: String)

    suspend fun changeCategoryColor(categoryId: Long, newColorId: Long)

    suspend fun changeCategoriesOrder(listId: Long, newOrderByIds: List<Long>)

    // Color Operations --------------------------------------------------
    suspend fun createColor(name: String, red: Float, green: Float, blue: Float): Long

    suspend fun createColors(colors: List<PixColor>)

    suspend fun deleteColorById(colorId: Long)

    suspend fun deleteUnusedColors(): Int

    suspend fun renameColor(colorId: Long, newName: String)

    suspend fun changeColor(colorId: Long, newRed: Float, newGreen: Float, newBlue: Float)

    fun getAllColors(): Flow<List<PixColor>>

    fun getAllColorsWithUses(): Flow<Map<PixColor, Int>>

    // Entry Operations --------------------------------------------------
    suspend fun setEntry(listId: Long, categoryIds: List<Long>, date: LocalDate): List<Long>

    suspend fun deleteEntry(listId: Long, date: LocalDate)

    // Import/Export Operations -------------------------------------------
    suspend fun exportAllData(): JsonElement

    suspend fun importAllData(data: JsonElement): Result<Unit>

    // Widget Operations ----------------------------------------------------------------------------------------------
    fun getAllWidgets(): Flow<List<PixDashboardWidget>>

    suspend fun createWidget(listId: Long, type: WidgetType, categories: List<PixCategory>): Long

    suspend fun updateWidget(widgetId: Long, categories: List<PixCategory>)

    suspend fun deleteWidget(widgetId: Long)
}
