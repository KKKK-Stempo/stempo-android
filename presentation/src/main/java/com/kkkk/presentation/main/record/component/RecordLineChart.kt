package com.kkkk.presentation.main.record.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.Purple10
import com.kkkk.presentation.main.theme.Purple50


@Composable
fun RecordLineChart(
    modifier: Modifier = Modifier,
    dateList: List<String> = emptyList(),
    entriesList: List<Entry> = emptyList(),
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                data = LineData(LineDataSet(entriesList, null).setDataSettings())
                setXAxisSettings(dateList)
                setYAxisSettings()
                setCommonSettings()
                invalidate()
            }
        }
    )
}

private fun LineDataSet.setDataSettings(): LineDataSet = apply {
    color = Purple50.toArgb()
    circleRadius = 8f
    setCircleColor(Purple50.toArgb())
    setDrawFilled(true)
    setDrawValues(false)
    lineWidth = 4F
    setDrawCircleHole(false)
    fillColor = Purple10.toArgb()
    fillAlpha = 255
    isHighlightEnabled = false
    mode = LineDataSet.Mode.LINEAR
}

private fun LineChart.setXAxisSettings(dateList: List<String>) {
    xAxis.apply {
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dateList.getOrNull(value.toInt()).orEmpty()
            }
        }
        position = XAxis.XAxisPosition.BOTTOM
        granularity = 1f
        axisMinimum = 0.7f
        axisMaximum = dateList.size - 0.8f
        setDrawGridLines(false)
        setDrawAxisLine(false)
        textSize = 15f
        textColor = Gray600.toArgb()
    }
}

private fun LineChart.setYAxisSettings() {
    axisLeft.apply {
        isEnabled = false
        axisMaximum = 100f
    }
    axisRight.isEnabled = false
}

private fun LineChart.setCommonSettings() {
    legend.isEnabled = false
    description.isEnabled = false
    setScaleEnabled(false)
    setDragEnabled(false)
    setExtraOffsets(0f, 0f, 0f, 20f)
}

@Preview(showBackground = true)
@Composable
fun RecordLineChartPreview() {
    RecordLineChart(
        dateList = listOf("1", "2", "3"),
        entriesList = listOf(Entry(0F, 70F), Entry(1F, 80F), Entry(2F, 60F))
    )
}