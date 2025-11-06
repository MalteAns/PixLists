package de.malteans.pixlists.core.data.repository

import de.malteans.pixlists.core.data.database.PixDao
import de.malteans.pixlists.core.data.database.entities.PixCategoryEntity
import de.malteans.pixlists.core.data.database.entities.PixColorEntity
import de.malteans.pixlists.core.data.database.entities.PixEntryEntity
import de.malteans.pixlists.core.data.database.entities.PixListEntity
import de.malteans.pixlists.core.data.mappers.toDomain
import de.malteans.pixlists.core.data.mappers.toEntity
import de.malteans.pixlists.core.data.mappers.toJsonDto
import de.malteans.pixlists.core.data.mappers.toPixList
import de.malteans.pixlists.core.data.serialization.JsonFullDataDto
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.domain.PixRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DefaultPixRepository(
    private val dao: PixDao,
) : PixRepository {

    // List Operations ----------------------------------------------------------------------------
    override suspend fun createList(name: String): Long {
        return dao.upsertList(PixListEntity(name = name))
    }

    override suspend fun deleteListById(listId: Long) {
        dao.deleteListById(listId)
    }

    override suspend fun renameList(listId: Long, newName: String) {
        dao.renameList(listId, newName)
    }

    override fun getAllPixListsWithoutData(): Flow<List<PixList>> {
        return dao.getAllLists().map { allLists ->
            allLists.map { listEntity ->
                listEntity.toPixList()
            }
        }
    }

    override fun getCurrentPixList(listId: Long): Flow<PixList?> {
        return dao.getListDeepRows(listId).map { rows ->
            if (rows.isEmpty()) return@map null

            val base = rows.first()

            val years: List<Int> = runCatching {
                Json.decodeFromString<List<Int>>(base.listYearsJson)
            }.getOrElse {
                // Fallback to current year if parsing fails
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year.let { listOf(it) }
            }

            // Build categories (unique by categoryId), include nullable color
            val categoriesById = linkedMapOf<Long, PixCategory>()
            rows.forEach { r ->
                val cid = r.categoryId ?: return@forEach
                if (!categoriesById.containsKey(cid)) {
                    val color: PixColor? = if (r.colorId != null && r.colorRed != null && r.colorGreen != null && r.colorBlue != null) {
                        PixColor(
                            id = r.colorId,
                            name = r.colorName.orEmpty(),
                            red = r.colorRed,
                            green = r.colorGreen,
                            blue = r.colorBlue
                        )
                    } else null

                    categoriesById[cid] = PixCategory(
                        id = cid,
                        listId = base.listId,
                        color = color,
                        name = r.categoryName.orEmpty(),
                        orderIndex = r.categoryOrderIndex ?: Int.MAX_VALUE
                    )
                }
            }

            // Sorted categories (by orderIndex)
            val sortedCategories = categoriesById.values.sortedBy { it.orderIndex }

            // Entries: Map<LocalDate, List<PixCategory>>
            val entriesMap: Map<LocalDate, List<PixCategory>> =
                rows.asSequence()
                    .filter { it.entryDate != null && it.categoryId != null }
                    .groupBy { it.entryDate!! }
                    .mapValues { (_, dayRows) ->
                        dayRows
                            .distinctBy { it.categoryId } // guard against accidental duplicates
                            .sortedBy { it.categoryOrderIndex ?: Int.MAX_VALUE }
                            .mapNotNull { r -> r.categoryId?.let(categoriesById::get) }
                    }

            PixList(
                id = base.listId,
                name = base.listName,
                categories = sortedCategories,
                entries = entriesMap,
                years = years
            )
        }
    }

    // Category Operations ----------------------------------------------
    override suspend fun createCategory(listId: Long, colorId: Long, name: String): Long {
        // Determine orderIndex based on current number of categories
        val orderIndex = dao.getCategoryCountForList(listId)
        return dao.upsertCategory(PixCategoryEntity(
            listId = listId,
            colorId = colorId,
            name = name,
            orderIndex = orderIndex
        ))
    }

    override suspend fun deleteCategoryById(categoryId: Long) {
        dao.deleteCategoryById(categoryId)
    }

    override suspend fun renameCategory(categoryId: Long, newName: String) {
        dao.renameCategory(categoryId, newName)
    }

    override suspend fun changeCategoryColor(categoryId: Long, newColorId: Long) {
        dao.changeCategoryColor(categoryId, newColorId)
    }

    override suspend fun changeCategoriesOrder(listId: Long, newOrderByIds: List<Long>) {
        newOrderByIds.forEachIndexed { orderIndex, categoryId ->
            dao.changeCategoryOrderIndex(categoryId, orderIndex)
        }
    }

    // Color Operations --------------------------------------------------
    override suspend fun createColor(name: String, red: Float, green: Float, blue: Float): Long {
        return dao.upsertColor(PixColorEntity(name = name, red = red, green = green, blue = blue))
    }

    override suspend fun createColors(colors: List<PixColor>) {
        colors.forEach { color ->
            dao.upsertColor(color.toEntity())
        }
    }

    override suspend fun deleteColorById(colorId: Long) {
        dao.deleteColorById(colorId)
    }

    override suspend fun deleteUnusedColors(): Int {
        return dao.deleteUnusedColors()
    }

    override suspend fun renameColor(colorId: Long, newName: String) {
        dao.renameColor(colorId, newName)
    }

    override suspend fun changeColor(colorId: Long, newRed: Float, newGreen: Float, newBlue: Float) {
        dao.changeColor(colorId, newRed, newGreen, newBlue)
    }

    override fun getAllColors(): Flow<List<PixColor>> {
        return dao.getAllColors().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllColorsWithUses(): Flow<Map<PixColor, Int>> {
        return combine(
            dao.getAllColors(),
            dao.getAllCategories(),
        ) { allColors, allCategories ->
            allColors.associate { color ->
                color.toDomain() to
                allCategories.count { category ->
                    category.colorId == color.id
                }
            }
        }
    }

    // Entry Operations --------------------------------------------------
    override suspend fun setEntry(listId: Long, categoryIds: List<Long>, date: LocalDate): List<Long> {
        val listEntity = dao.getListWithoutUpdate(listId)
            ?: throw IllegalArgumentException("List with id $listId does not exist")
        val list = listEntity.toPixList()
        if (!list.years.contains(date.year)) {
            dao.upsertList(
                listEntity.copy(
                    years = Json.encodeToString(list.years + date.year)
                )
            )
        }

        val currentEntries
            = dao.getEntriesWithoutUpdate(listId, date).associateBy { it.categoryId }.toMutableMap()
        val entryIds = mutableListOf<Long>()
        for (id in categoryIds) {
            if (id in currentEntries.keys) {
                currentEntries.remove(id)
                entryIds.add(id)
            } else {
                entryIds.add(dao.upsertEntry(
                    PixEntryEntity(
                        listId = listId,
                        date = date,
                        categoryId = id
                    )
                ))
            }
        }
        for (entry in currentEntries.values) {
            dao.deleteEntryById(entry.id)
        }
        return entryIds
    }

    override suspend fun deleteEntry(listId: Long, date: LocalDate) {
        dao.deleteEntryByListIdAndDate(listId, date)
    }

    // Import/Export Operations -------------------------------------------
    override suspend fun exportAllData(): JsonElement {
        val allColors = dao.getAllColorsWithoutUpdate().associateBy { it.id }
        val allLists = dao.getAllLists().first()
        val allCategories = dao.getAllCategoriesWithoutUpdate()
        val allEntries = dao.getAllEntriesWithoutUpdate()

        val listsWithData = allLists.map { listEntity ->
            PixList(
                id = listEntity.id,
                name = listEntity.name,
                categories = allCategories
                    .filter { categoryEntity -> categoryEntity.listId == listEntity.id }
                    .map { categoryEntity -> categoryEntity.toDomain(allColors[categoryEntity.colorId]?.toDomain()) },
                entries = allEntries
                    .filter { entryEntity -> entryEntity.listId == listEntity.id }
                    .groupBy(
                        keySelector = { it.date },
                        valueTransform = { allCategories.find { cat -> cat.id == it.categoryId }?.toDomain(allColors[it.categoryId]?.toDomain())
                            ?: throw IllegalStateException("Category ${it.categoryId} not found for entry ${it.id} on ${it.date}") }
                    )
            )
        }

        val fullDataDto = JsonFullDataDto(
            colors = allColors.map { (_, colorEntity) ->
                colorEntity.toJsonDto()
            },
            lists = listsWithData.map { list ->
                list.toJsonDto()
            }
        )

        return Json.encodeToJsonElement(fullDataDto)
    }

    override suspend fun importAllData(data: JsonElement): Result<Unit> {
        val allCurrentColors = dao.getAllColorsWithoutUpdate().associateBy { it.name }
        val allCurrentLists = dao.getAllLists().first().associateBy { it.name }

        val fullDataDto = try {
            Json.decodeFromJsonElement<JsonFullDataDto>(data)
        } catch (e: Exception) {
            return Result.failure(IllegalArgumentException("Failed to decode import data", e))
        }
        val colorsMap = fullDataDto.colors.associate {
            it.name to if (it.name in allCurrentColors.keys) {
                val currentColor = allCurrentColors[it.name]!!
                if (currentColor.red != it.red || currentColor.green != it.green || currentColor.blue != it.blue)
                    dao.upsertColor(it.toEntity().copy(name = it.name + " (imported)"))
                else
                    currentColor.id
            } else {
                dao.upsertColor(it.toEntity())
            }
        }
        fullDataDto.lists.forEach { listDto ->
            val listId = if (listDto.name !in allCurrentLists.keys) dao.upsertList(listDto.toEntity())
                else dao.upsertList(listDto.toEntity().copy(name = listDto.name + " (imported)"))

            val categoriesMap = listDto.categories.associate { categoryDto ->
                categoryDto.name to dao.upsertCategory(categoryDto.toEntity(
                    listId = listId,
                    colorId = colorsMap[categoryDto.colorName]
                        ?: return Result.failure(IllegalStateException("Color '${categoryDto.colorName}' not found for category '${categoryDto.name}' in list '${listDto.name}'")),
                ))
            }
            listDto.entries.forEach { entryDto ->
                val date = LocalDate.fromEpochDays(entryDto.epochDays)
                entryDto.categoryNames.forEach { categoryName ->
                    dao.upsertEntry(PixEntryEntity(
                        listId = listId,
                        date = date,
                        categoryId = categoriesMap[categoryName]
                            ?: return Result.failure(IllegalStateException("Category '$categoryName' not found for entry on $date in list '${listDto.name}'")),
                    ))
                }
            }
        }
        return Result.success(Unit)
    }
}
