package de.malteans.pixlists.lists.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.presentation.components.Dropdown
import de.malteans.pixlists.core.presentation.components.customIcons.FilledPixIcon
import de.malteans.pixlists.core.presentation.components.customIcons.OutlinedPixIcon
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.category

@Composable
fun CategoryListItem(
    selectedCategory: PixCategory?,
    options: Map<PixCategory, String>,
    changeCategory: (PixCategory) -> Unit,
    removeCategory: () -> Unit,
    index: Int,
    initialExpanded: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Dropdown(
            options = options as Map<Any, String>,
            label = "${stringResource(Res.string.category)} $index",
            onValueChanged = { changeCategory(it as PixCategory) },
            selectedOption = Pair(selectedCategory, selectedCategory?.name ?: ""),
            optionIcon = { category ->
                if (category != null) {
                    category as PixCategory
                    if (category.color != null) {
                        Icon(
                            imageVector = FilledPixIcon,
                            contentDescription = "Filled Pix",
                            tint = category.color.toColor(),
                        )
                    } else {
                        Icon(
                            imageVector = OutlinedPixIcon,
                            contentDescription = "Outlined Pix",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            initialExpanded = initialExpanded,
            modifier = Modifier
                .weight(1f)
        )
        IconButton(
            onClick = removeCategory,
            enabled = selectedCategory != null,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Delete Category",
                tint = if (selectedCategory == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}