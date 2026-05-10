package de.malteans.pixlists.core.data.serialization

import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.serialization.Serializable

@Serializable
data class JsonCategoryDto (
    val name: String,
    val colorName: String,
    val orderIndex: Int,
    val enableWeight: Boolean? = PixCategory.DEFAULT_ENABLE_WEIGHT,
    val minWeight: Int? = PixCategory.DEFAULT_MIN_WEIGHT,
    val maxWeight: Int? = PixCategory.DEFAULT_MAX_WEIGHT,
    val weightStep: Int? = PixCategory.DEFAULT_WEIGHT_STEP,
)
