package de.malteans.pixlists.core.presentation.main.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.domain.PixColor
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.*

@Composable
fun StartColorsDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (List<PixColor>) -> Unit,
    availableColors: List<PixColor> = defaultStartColors,
) {
    val indication = LocalIndication.current

    val selectedColors = remember { mutableStateListOf<PixColor>() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(selectedColors)
                    onDismissRequest()
                },
                enabled = selectedColors.isNotEmpty()
            ) {
                Text(stringResource(Res.string.add_colors))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.cancel))
            }
        },
        title = {
            Text(stringResource(Res.string.select_start_colors))
        },
        text = {
            LazyColumn(
                verticalArrangement = spacedBy(4.dp),
            ) {
                items(availableColors) { color ->
                    val isSelected = selectedColors.contains(color)

                    val onClick: () -> Unit = {
                        if (isSelected) selectedColors.remove(color)
                        else selectedColors.add(color)
                    }

                    val sharedInteractionSource = remember { MutableInteractionSource() }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .indication(sharedInteractionSource, indication)
                            .clickable(
                                indication = null,
                                interactionSource = sharedInteractionSource,
                                onClick = onClick
                            )
                            .background(color.toColor())
                            .fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onClick() },
                            interactionSource = sharedInteractionSource,
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = MaterialTheme.colorScheme.onSurface,
                                uncheckedColor = color.toColor(),
                                checkedColor = color.toColor(),
                            ),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = color.name,
                        )
                    }
                }
            }
        }
    )
}

val defaultStartColors: List<PixColor>
    @Composable get() = listOf(
        PixColor(name = stringResource(Res.string.peach),          red = 1.0f,  green = 0.87f, blue = 0.77f),
        PixColor(name = stringResource(Res.string.lemon_yellow),    red = 1.0f,  green = 0.97f, blue = 0.69f),
        PixColor(name = stringResource(Res.string.mint_green),      red = 0.74f, green = 0.98f, blue = 0.79f),
        PixColor(name = stringResource(Res.string.baby_blue),       red = 0.68f, green = 0.90f, blue = 1.0f),
        PixColor(name = stringResource(Res.string.lavender),        red = 0.82f, green = 0.75f, blue = 0.93f),
        PixColor(name = stringResource(Res.string.blush_pink),      red = 1.0f,  green = 0.82f, blue = 0.86f),
        PixColor(name = stringResource(Res.string.pale_orange),     red = 1.0f,  green = 0.85f, blue = 0.72f),
        PixColor(name = stringResource(Res.string.soft_chartreuse), red = 0.94f, green = 0.98f, blue = 0.74f),
        PixColor(name = stringResource(Res.string.pastel_emerald),  red = 0.76f, green = 0.96f, blue = 0.86f),
        PixColor(name = stringResource(Res.string.sea_mist),        red = 0.74f, green = 0.90f, blue = 0.88f),
        PixColor(name = stringResource(Res.string.powder_sky),      red = 0.78f, green = 0.93f, blue = 0.98f),
        PixColor(name = stringResource(Res.string.periwinkle),      red = 0.80f, green = 0.82f, blue = 0.95f),
        PixColor(name = stringResource(Res.string.soft_iris),       red = 0.88f, green = 0.82f, blue = 0.96f),
        PixColor(name = stringResource(Res.string.mauve_mist),      red = 0.93f, green = 0.80f, blue = 0.93f),
        PixColor(name = stringResource(Res.string.orchid_bloom),    red = 0.96f, green = 0.82f, blue = 0.92f),
        PixColor(name = stringResource(Res.string.soft_plum),       red = 0.95f, green = 0.82f, blue = 0.95f),
        PixColor(name = stringResource(Res.string.golden_cream),    red = 0.99f, green = 0.93f, blue = 0.74f),
    )
