package com.kkkk.presentation.main.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(R.layout.fragment_report) {

    private val viewModel by activityViewModels<ReportViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setGraphData()
        setGraphSettings()
    }

    private fun setGraphData() {
        viewModel.setGraphValue()
        binding.chartReport.apply {
            data = LineData(LineDataSet(viewModel.chartEntry, CHART_REPORT).setDataSettings())
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
            setXAxisSettings(this)
            setYAxisSettings(this)
            setCommonSettings(this)
        }
    }

    private fun setXAxisSettings(chart: LineChart) {
        chart.xAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    return if (value.toInt() in viewModel.mockList.indices) {
                        DATE_FORMAT.parse(viewModel.mockList[value.toInt()].second)
                            ?.let { DISPLAY_DATE_FORMAT.format(it) }
                    } else {
                        ""
                    }
                }
            }
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            axisMinimum = 0.7f
            axisMaximum = viewModel.chartEntry.size - 0.8f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textSize = 15f
            textColor = colorOf(R.color.gray_600)
        }
    }

    private fun setYAxisSettings(chart: LineChart) {
        chart.axisLeft.apply {
            isEnabled = false
            axisMaximum = 100f
        }
        chart.axisRight.isEnabled = false
    }

    private fun setCommonSettings(chart: LineChart) {
        chart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            setScaleEnabled(false)
            setDragEnabled(false)
            setExtraOffsets(0f, 0f, 0f, 20f)
        }
    }

    companion object {
        private const val CHART_REPORT = "CHART_REPORT"
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val DISPLAY_DATE_FORMAT = SimpleDateFormat("MM/dd", Locale.getDefault())
    }
}