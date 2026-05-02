package de.malteans.pixlists.dashboard.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.presentation.components.Dropdown
import de.malteans.pixlists.lists.presentation.view.components.PixCellCanvas
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.add_entry_today

@Composable
fun QuickEntryWidget(
    categories: List<PixCategory>,
    onAddEntry: (PixCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Dropdown(
            selectedOption = Pair(selectedCategory, selectedCategory?.name ?: "—"),
            options = categories.associateWith { it.name },
            onValueChanged = { selectedCategory = it },
            optionIcon = {
                it?.let { category ->
                    PixCellCanvas(
                        categories = listOf(category),
                        animation = false,
                    )
                }
            },
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = { selectedCategory?.let { onAddEntry(it) } },
            enabled = selectedCategory != null,
        ) {
            Icon(
                imageVector = Icons.Default.AddBox,
                contentDescription = stringResource(Res.string.add_entry_today),
            )
        }
    }
}