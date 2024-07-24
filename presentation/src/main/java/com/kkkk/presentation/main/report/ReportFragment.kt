package com.kkkk.presentation.main.report

import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(R.layout.fragment_report) {

    val chartEntry = arrayListOf<Entry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
    private val mockList = listOf(
        Pair(10f, "2024-02-28"),
        Pair(20f, "2024-03-28"),
        Pair(30f, "2024-04-29"),
        Pair(100f, "2024-05-23"),
        Pair(90f, "2024-06-13")
    )

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setGraphData()
        setGraphSettings()
    }

    private fun setGraphData() {
        mockList.forEachIndexed { index, (value, _) ->
            chartEntry.add(Entry(index.toFloat(), value))
        }
        binding.chartReport.apply {
            data = LineData(LineDataSet(chartEntry, CHART_REPORT).setDataSettings())
            invalidate()
        }
    }

    private fun LineDataSet.setDataSettings(): LineDataSet {
        this.apply {
            color = colorOf(R.color.purple_50)
            circleRadius = 8f
            setCircleColor(colorOf(R.color.purple_50))
            setDrawFilled(true)
            setDrawValues(false)
            lineWidth = 4F
            setDrawCircleHole(false)
            fillColor = colorOf(R.color.purple_10)
            fillAlpha = 255
            isHighlightEnabled = false
            mode = LineDataSet.Mode.LINEAR
        }
        return this
    }

    private fun setGraphSettings() {
        binding.chartReport.apply {
            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in mockList.indices) {
                            displayDateFormat.format(dateFormat.parse(mockList[index].second))
                        } else {
                            ""
                        }
                    }
                }
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                axisMinimum = 0.7f
                axisMaximum = mockList.size - 0.8f
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textSize = 15f
                textColor = colorOf(R.color.gray_600)
            }
            axisLeft.apply {
                isEnabled = false
                axisMaximum = 100f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setScaleEnabled(false)
            setDragEnabled(false)
            setExtraOffsets(0f, 0f, 0f, 20f)
        }
    }

    companion object {
        private const val CHART_REPORT = "CHART_REPORT"
    }
}