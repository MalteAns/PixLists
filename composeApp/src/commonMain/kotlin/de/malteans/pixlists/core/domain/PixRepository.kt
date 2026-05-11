package de.malteans.pixlists.core.domain

import de.malteans.pixlists.dashboard.domain.WidgetData
import de.malteans.pixlists.dashboard.domain.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.JsonElement

interface PixRepository {

    // List Operations --------------------------------------------------
    suspend fun createList(name: String): Long

    suspend fun deleteListById(listId: Long)

    suspend fun renameList(listId: Long, newName: String)

    fun getAllPixLists(): Flow<List<PixList>>
    fun getAllPixListsWithoutData(): Flow<List<PixList>>

    suspend fun addYearToList(listId: Long, year: Int)

    /** PixList with all it's info */
    fun getCurrentPixList(listId: Long): Flow<PixList?>

    // Category Operations ----------------------------------------------
    suspend fun createCategory(
        listId: Long, colorId: Long, name: String,
        enableWeight: Boolean = PixCategory.DEFAULT_ENABLE_WEIGHT, minWeight: Int = PixCategory.DEFAULT_MIN_WEIGHT,
        maxWeight: Int = PixCategory.DEFAULT_MAX_WEIGHT, weightStep: Int = PixCategory.DEFAULT_WEIGHT_STEP,
    ): Long

    suspend fun updateCategory(category: PixCategory)

    suspend fun deleteCategoryById(categoryId: Long)

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
    suspend fun setEntry(listId: Long, entries: List<PixEntry>, date: LocalDate): List<Long>

    suspend fun deleteEntry(listId: Long, date: LocalDate)

    // Import/Export Operations -------------------------------------------
    suspend fun exportAllData(): JsonElement

    suspend fun importAllData(data: JsonElement): Result<Unit>

    // Widget Operations ----------------------------------------------------------------------------------------------
    fun getAllWidgets(): Flow<List<WidgetData>>

    suspend fun createWidget(listId: Long, type: WidgetType, categoryIds: List<Long>): Long

    suspend fun updateWidget(widgetId: Long, pixListId: Long, widgetType: WidgetType, categoryIds: List<Long>)

    suspend fun updateWidgetOrder(widgetIds: List<Long>)

    suspend fun deleteWidget(widgetId: Long)
}
