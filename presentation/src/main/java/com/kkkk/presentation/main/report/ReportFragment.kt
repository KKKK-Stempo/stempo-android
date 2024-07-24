package com.kkkk.presentation.main.report

import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentReportBinding

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(R.layout.fragment_report) {

    val chartEntry = arrayListOf<Entry>()
    val mockList = listOf(10, 30, 50, 60, 90)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setGraphData()
        setGraphSettings()
    }

    private fun setGraphData() {
        chartEntry.add(Entry(-1f, mockList[0].toFloat()))
        mockList.forEachIndexed { index, listItem ->
            chartEntry.add(Entry(index.toFloat(), listItem.toFloat()))
        }
        binding.chartReport.apply {
            data = LineData(LineDataSet(chartEntry, CHART_REPORT).apply {
                color = colorOf(R.color.purple_50)
                circleRadius = 8f
                setCircleColor(colorOf(R.color.purple_50))
                setDrawFilled(true)
                setDrawValues(false)
                lineWidth = 3F
                setDrawCircleHole(false)
                fillColor = colorOf(R.color.purple_10)
                isHighlightEnabled = false
                mode = LineDataSet.Mode.LINEAR
            })
            invalidate()
        }
    }

    private fun setGraphSettings() {
        binding.chartReport.apply {
            xAxis.apply {
                isEnabled = false
                axisMinimum = 0.7f
                axisMaximum = mockList.size - 0.8f
            }
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setScaleEnabled(false)
            setDragEnabled(false)
        }
    }

    companion object {
        private const val CHART_REPORT = "CHART_REPORT"
    }
}