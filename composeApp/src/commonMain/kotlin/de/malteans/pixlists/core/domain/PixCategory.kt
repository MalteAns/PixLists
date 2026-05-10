package de.malteans.pixlists.core.domain

data class PixCategory(
    val id: Long,
    val listId: Long,
    val color: PixColor?,
    val name: String,
    val orderIndex: Int = DEFAULT_ORDER_INDEX,
    val enableWeight: Boolean = DEFAULT_ENABLE_WEIGHT,
    val minWeight: Int = DEFAULT_MIN_WEIGHT,
    val maxWeight: Int = DEFAULT_MAX_WEIGHT,
    val weightStep: Int = DEFAULT_WEIGHT_STEP,
) {
    companion object {
        const val DEFAULT_ORDER_INDEX = Int.MAX_VALUE
        const val DEFAULT_ENABLE_WEIGHT = false
        const val DEFAULT_MIN_WEIGHT = 0
        const val DEFAULT_MAX_WEIGHT = 100
        const val DEFAULT_WEIGHT_STEP = 1
    }
}