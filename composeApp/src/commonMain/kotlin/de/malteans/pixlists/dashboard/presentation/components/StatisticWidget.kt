package de.malteans.pixlists.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import de.malteans.pixlists.core.domain.PixCategory
import de.malteans.pixlists.core.domain.PixEntry
import de.malteans.pixlists.core.domain.PixList
import de.malteans.pixlists.core.presentation.components.FadeForScrollList
import de.malteans.pixlists.core.presentation.util.*
import de.malteans.pixlists.dashboard.domain.WidgetType
import de.malteans.pixlists.lists.presentation.view.components.PixCellCanvas

@Composable
fun StatisticWidget(
    type: WidgetType,
    pixList: PixList,
    categories: List<PixCategory>,
    modifier: Modifier = Modifier,
) {
    when (type) {
        WidgetType.STATISTICS -> TextStatisticWidget(pixList, categories, modifier)
        WidgetType.STATISTICS_PIE -> { Text("Coming soon...") } // TODO
        WidgetType.STATISTICS_COLUMNS -> ColumnChartStatisticWidget(pixList, categories, modifier)
        WidgetType.STATISTICS_LINES -> LineChartStatisticWidget(pixList, categories, modifier)
        else -> throw IllegalArgumentException("Invalid widget type for StatisticWidget: $type")
    }
}

@Composable
private fun TextStatisticWidget(
    pixList: PixList,
    categories: List<PixCategory>,
    modifier: Modifier = Modifier,
) {
    val absoluteMap = pixList.entries.getAbsoluteMap(categories)
    val relativeMap = absoluteMap.toRelativeMap()

    val scrollState = rememberScrollState()
    FadeForScrollList(
        showTopFade = scrollState.canScrollBackward,
        showBottomFade = scrollState.canScrollForward,
        fadeColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = modifier
                .heightIn(max = 200.dp)
                .verticalScroll(scrollState)
        ) {
            categories.forEach { category ->
                val absCount = absoluteMap[category] ?: 0
                val relCount = relativeMap[category] ?: 0.0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    PixCellCanvas(
                        entries = listOf(PixEntry(category)),
                        animation = false,
                    )
                    Text(
                        text = category.name,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "$absCount (${(relCount * 100).fastRoundToInt()}%)")
                }
            }
        }
    }
}

@Composable
private fun ColumnChartStatisticWidget(
    pixList: PixList,
    categories: List<PixCategory>,
    modifier: Modifier = Modifier,
) {
    val absoluteMap by remember(pixList, categories) { derivedStateOf {
        pixList.entries.getAbsoluteMap(categories)
    } }

    ColumnChart(
        dataMap = absoluteMap.mapValues { it.value.toDouble() },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    )
}

@Composable
private fun LineChartStatisticWidget(
    pixList: PixList,
    categories: List<PixCategory>,
    modifier: Modifier = Modifier,
) {
    val lineChartData by remember(pixList, categories) { derivedStateOf {
        pixList.entries.getLineChartData(categories)
    } }

    LineChart(
        data = lineChartData,
        modifier = modifier
    )
}
