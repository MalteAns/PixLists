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
import de.malteans.pixlists.core.presentation.util.highestContrastColor
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
                        val contentColor = highestContrastColor(
                            color.toColor(),
                            listOf(
                                MaterialTheme.colorScheme.onSurface,
                                MaterialTheme.colorScheme.inverseOnSurface,
                            )
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onClick() },
                            interactionSource = sharedInteractionSource,
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = contentColor,
                                uncheckedColor = color.toColor(),
                                checkedColor = color.toColor(),
                            ),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = color.name,
                            color = contentColor,
                        )
                    }
                }
            }
        }
    )
}

val defaultStartColors: List<PixColor>
    @Composable get() = listOf(
        // Light pastels
        PixColor(name = stringResource(Res.string.cotton_candy),     red = 1.00f, green = 0.86f, blue = 0.93f),
        PixColor(name = stringResource(Res.string.lemon_sorbet),      red = 1.00f, green = 0.98f, blue = 0.75f),
        PixColor(name = stringResource(Res.string.mint_mousse),       red = 0.78f, green = 0.98f, blue = 0.84f),
        PixColor(name = stringResource(Res.string.seafoam_mist),      red = 0.78f, green = 0.94f, blue = 0.90f),
        PixColor(name = stringResource(Res.string.sky_powder),        red = 0.80f, green = 0.93f, blue = 0.98f),
        PixColor(name = stringResource(Res.string.lilac_haze),        red = 0.88f, green = 0.82f, blue = 0.96f),
        PixColor(name = stringResource(Res.string.apricot_cream),     red = 1.00f, green = 0.88f, blue = 0.77f),
        PixColor(name = stringResource(Res.string.buttercream),       red = 1.00f, green = 0.95f, blue = 0.82f),
        PixColor(name = stringResource(Res.string.pistachio_whisper), red = 0.86f, green = 0.95f, blue = 0.80f),
        PixColor(name = stringResource(Res.string.aqua_glass),        red = 0.78f, green = 0.96f, blue = 0.96f),
        PixColor(name = stringResource(Res.string.powder_denim),      red = 0.82f, green = 0.89f, blue = 0.96f),
        PixColor(name = stringResource(Res.string.orchid_veil),       red = 0.96f, green = 0.86f, blue = 0.96f),
        PixColor(name = stringResource(Res.string.rose_quartz),       red = 0.98f, green = 0.86f, blue = 0.90f),
        PixColor(name = stringResource(Res.string.peach_fuzz),        red = 1.00f, green = 0.89f, blue = 0.82f),
        PixColor(name = stringResource(Res.string.melon_sherbet),     red = 0.99f, green = 0.89f, blue = 0.78f),

        // Darker (muted) pastels
        PixColor(name = stringResource(Res.string.dusty_peach),       red = 0.93f, green = 0.74f, blue = 0.69f),
        PixColor(name = stringResource(Res.string.muted_coral),       red = 0.90f, green = 0.67f, blue = 0.67f),
        PixColor(name = stringResource(Res.string.dusty_rose),        red = 0.86f, green = 0.70f, blue = 0.76f),
        PixColor(name = stringResource(Res.string.mauve_stone),       red = 0.78f, green = 0.68f, blue = 0.78f),
        PixColor(name = stringResource(Res.string.dusk_lavender),     red = 0.74f, green = 0.68f, blue = 0.82f),
        PixColor(name = stringResource(Res.string.storm_periwinkle),  red = 0.68f, green = 0.72f, blue = 0.86f),
        PixColor(name = stringResource(Res.string.slate_powder),      red = 0.68f, green = 0.76f, blue = 0.84f),
        PixColor(name = stringResource(Res.string.muted_teal),        red = 0.64f, green = 0.80f, blue = 0.78f),
        PixColor(name = stringResource(Res.string.sage_moss),         red = 0.73f, green = 0.80f, blue = 0.70f),
        PixColor(name = stringResource(Res.string.muted_olive),       red = 0.76f, green = 0.78f, blue = 0.64f),
        PixColor(name = stringResource(Res.string.dusty_mustard),     red = 0.84f, green = 0.76f, blue = 0.58f),
        PixColor(name = stringResource(Res.string.clay_apricot),      red = 0.86f, green = 0.72f, blue = 0.58f),
        PixColor(name = stringResource(Res.string.cocoa_cream),       red = 0.74f, green = 0.64f, blue = 0.60f),
        PixColor(name = stringResource(Res.string.foggy_taupe),       red = 0.74f, green = 0.70f, blue = 0.66f),
        PixColor(name = stringResource(Res.string.ink_wash),          red = 0.58f, green = 0.62f, blue = 0.66f),
    )

