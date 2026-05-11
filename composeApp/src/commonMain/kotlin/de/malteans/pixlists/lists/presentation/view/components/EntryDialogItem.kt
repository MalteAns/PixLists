package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.core.presentation.components.Dropdown
import de.malteans.pixlists.core.presentation.components.IconIconButton
import de.malteans.pixlists.core.presentation.theme.PixListsTheme
import de.malteans.pixlists.core.presentation.theme.containerColor
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.category
import pixlists.composeapp.generated.resources.delete_category
import pixlists.composeapp.generated.resources.weight_label
import kotlin.math.roundToInt

@Composable
fun EntryDialogItem(
    index: Int,
    selectedCategory: PixCategory?,
    options: Map<PixCategory, String>,
    changeCategory: (PixCategory) -> Unit,
    weight: Int?,
    maxWeights: Map<Long, Int>, // CategoryId to maxWeight
    onWeightChange: (Int) -> Unit,
    removeEntry: () -> Unit,
    initialExpanded: Boolean = false,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            @Suppress("UNCHECKED_CAST")
            Dropdown(
                options = options,
                label = "${stringResource(Res.string.category)} $index",
                onValueChanged = { changeCategory(it) },
                selectedOption = Pair(selectedCategory, selectedCategory?.name ?: ""),
                optionIcon = { selection ->
                    PixCellCanvas(
                        entries = selection?.let { category ->
                            listOf(
                                PixEntry(
                                    category = category,
                                    weight = weight,
                                )
                            )
                        } ?: emptyList(),
                        maxWeights = maxWeights,
                        animation = false,
                    )
                },
                initialExpanded = initialExpanded,
                modifier = Modifier.weight(1f)
            )
            IconIconButton(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(Res.string.delete_category),
                iconColor = if (selectedCategory == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.onSurface,
                onClick = removeEntry,
                enabled = selectedCategory != null,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        AnimatedVisibility(
            visible = selectedCategory != null && selectedCategory.enableWeight,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.weight_label, weight ?: selectedCategory?.maxWeight ?: 0),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.animateContentSize()
                )
                Spacer(Modifier.width(8.dp))
                if (selectedCategory != null) {
                    val categoryColor = selectedCategory.color?.toColor() ?: Color.Unspecified
                    Slider(
                        value = (weight ?: selectedCategory.maxWeight).toFloat(),
                        onValueChange = { onWeightChange(it.roundToInt()) },
                        valueRange = selectedCategory.minWeight.toFloat()..selectedCategory.maxWeight.toFloat(),
                        steps = (((selectedCategory.maxWeight - selectedCategory.minWeight) / selectedCategory.weightStep) - 1).coerceAtLeast(0),
                        colors = SliderDefaults.colors(
                            thumbColor = categoryColor,
                            activeTrackColor = categoryColor,
                            inactiveTrackColor = MaterialTheme.colorScheme.containerColor,
                            activeTickColor = MaterialTheme.colorScheme.containerColor.copy(alpha = 0.4f),
                            inactiveTickColor = categoryColor.copy(alpha = 0.6f),
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    )
                } else {
                    Slider(
                        value = 0f,
                        onValueChange = {  },
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun EntryDialogItemPreviewContent() {
    Surface {
        EntryDialogItem(
            index = 1,
            selectedCategory = PixCategory(
                id = 1L,
                listId = 1L,
                name = "Test Category",
                color = PixColor(
                    name = "rot", red = 1f, green = 0f, blue = 0f,
                ),
                enableWeight = true,
                minWeight = 0,
                maxWeight = 100,
                weightStep = 10
            ),
            options = mapOf(),
            changeCategory = {},
            weight = 80,
            maxWeights = mapOf(
                1L to 100,
            ),
            onWeightChange = {},
            removeEntry = {}
        )
    }
}

@Preview
@Composable
fun EntryDialogItemPreviewDark() {
    PixListsTheme(useDarkTheme = true) {
        EntryDialogItemPreviewContent()
    }
}

@Preview
@Composable
fun EntryDialogItemPreviewLight() {
    PixListsTheme(useDarkTheme = false) {
        EntryDialogItemPreviewContent()
    }
}