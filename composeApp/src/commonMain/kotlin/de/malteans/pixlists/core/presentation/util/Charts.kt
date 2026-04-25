package de.malteans.pixlists.core.presentation.util

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.*
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.*
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import de.malteans.pixlists.core.domain.PixCategory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.no_data_available
import kotlin.math.roundToInt

private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()
private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}

@Composable
fun ColumnChart(
    dataMap: Map<PixCategory, Double>,
    color: Color = LocalContentColor.current,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    if (dataMap.isEmpty()) {
        Text(stringResource(Res.string.no_data_available))
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit, dataMap) {
        modelProducer.runTransaction {
            columnSeries {
                dataMap.values.forEachIndexed { index, categoryAmount ->
                    series(List(index) { 0 } + categoryAmount)
                }
            }
            extras { extraStore -> extraStore[BottomAxisLabelKey] = dataMap.keys.map { it.name } }
        }
    }
    ProvideVicoTheme(
        rememberM3VicoTheme(
            lineColor = color,
            textColor = color,
        )
    ) {
        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            dataMap.map { (category, _) ->
                                rememberLineComponent(
                                    fill = Fill(category.color?.toColor() ?: color),
                                    thickness = 16.dp,
                                    shape = MaterialTheme.shapes.small.copy(
                                        bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
                                    ),
                                )
                            }
                        ),
                        mergeMode = { ColumnCartesianLayer.MergeMode.Stacked }
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        itemPlacer = remember { VerticalAxis.ItemPlacer.step(step = { 1.0 }, shiftTopLines = false) },
                    ),
                    bottomAxis =
                        HorizontalAxis.rememberBottom(
                            itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
                            valueFormatter = BottomAxisValueFormatter,
                        ),
                    marker = rememberMarker(
                        valueFormatter = { _, targets ->
                            targets.firstOrNull()?.let { target ->
                                dataMap.toList()[target.x.roundToInt()].second.roundToInt().toString()
                            } ?: ""
                        },
                        textColor = color,
                        containerColor = containerColor,
                        showIndicator = false,
                    ),
                    layerPadding = { CartesianLayerPadding(scalableStart = 8.dp, scalableEnd = 8.dp) },
                ),
            modelProducer = modelProducer,
            modifier = modifier,
            scrollState = rememberVicoScrollState(scrollEnabled = true),
        )
    }
}

@Composable
fun LineChart(
    data: Map<PixCategory, List<Pair<LocalDate, Int>>>,
    color: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) {
    if (data.isEmpty()) {
        Text(stringResource(Res.string.no_data_available))
        return
    }

    val bottomAxisFormatter = CartesianValueFormatter { _, x, _ ->
        val date = LocalDate.fromEpochDays(x.toLong())
        "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit, data) {
        modelProducer.runTransaction {
            lineSeries {
                data.forEach { (_, categoryData) ->
                    series(categoryData.map { it.first.toEpochDays() }, categoryData.map { it.second })
                }
            }
        }
    }
    ProvideVicoTheme(
        rememberM3VicoTheme(
            lineColor = color,
            textColor = color,
        )
    ) {
        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        LineCartesianLayer.LineProvider.series(
                            data.map { (category, _) ->
                                LineCartesianLayer.rememberLine(
                                    LineCartesianLayer.LineFill.single(Fill(category.color?.toColor() ?: color))
                                )
                            }
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomAxisFormatter
                    ),
                    marker = rememberMarker(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    layerPadding = { CartesianLayerPadding(scalableStart = 2.dp, scalableEnd = 2.dp) },
                ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(
                scrollEnabled = true,
                initialScroll = Scroll.Absolute.End,
            ),
            zoomState = rememberVicoZoomState(
                initialZoom = remember { Zoom.fixed(0f) }
            ),
            modifier = modifier
        )
    }
}
