package de.malteans.pixlists.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.domain.PixCategory
import de.malteans.pixlists.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.presentation.components.customIcons.OutlinedPixIcon
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * @param[onUpdateOrder] Called after a drag finishes with the *new* order (list ids).
 */
@Composable
fun CategoryList(
    curCategories: List<PixCategory>,
    onEditCategory: (PixCategory) -> Unit,
    onCreateCategory: () -> Unit,
    onUpdateOrder: (List<Long>) -> Unit,
    modifier: Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    var items by remember { mutableStateOf(curCategories.sortedBy { it.orderIndex }) }

    LaunchedEffect(curCategories) {
        if (items != curCategories) {
            items = curCategories.sortedBy { it.orderIndex }
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        items = items.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        val newOrder = items.map { it.id }
        println("New order: $newOrder")
        onUpdateOrder(items.map { it.id })

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    Column(
        modifier = modifier
            .padding(start = 4.dp, top = 8.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = items,
                key = { it.id }
            ) { category ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = category.id
                ) { isDragging ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable { onEditCategory(category) }
                            .longPressDraggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            if (category.color != null) {
                                Icon(
                                    imageVector = FilledPixIcon,
                                    contentDescription = "Pix Category Icon",
                                    tint = category.color.toColor(),
                                )
                            } else {
                                Icon(
                                    imageVector = OutlinedPixIcon,
                                    contentDescription = "Empty Pix",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Row {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Non-reorderable "Add Category" item at the bottom
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top,
                ) {
                    IconButton(onClick = onCreateCategory) {
                        Icon(
                            imageVector = Icons.Outlined.AddBox,
                            contentDescription = "Add Category"
                        )
                    }
                }
            }
        }
    }
}
