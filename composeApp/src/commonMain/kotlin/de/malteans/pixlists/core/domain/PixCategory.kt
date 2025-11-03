package de.malteans.pixlists.core.domain

data class PixCategory(
    val id: Long,
    val listId: Long,
    val color: PixColor?,
    val name: String,
    val orderIndex: Int = -1,
)
