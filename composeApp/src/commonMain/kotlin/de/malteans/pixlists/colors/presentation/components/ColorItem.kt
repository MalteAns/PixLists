package de.malteans.pixlists.colors.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixColor
import de.malteans.pixlists.core.presentation.components.customIcons.FilledPixIcon

@Composable
fun ColorItem(
    color: PixColor,
    modifier: Modifier = Modifier,
    count: Int = 0,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Icon(
                imageVector = FilledPixIcon,
                contentDescription = "Preview Pix",
                tint = color.toColor()
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            Text(
                text = color.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = color.toHex(),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Column {
            Text(
                text = "$count",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}