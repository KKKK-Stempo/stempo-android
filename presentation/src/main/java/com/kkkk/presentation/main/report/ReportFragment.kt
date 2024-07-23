package com.kkkk.presentation.main.report

import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentReportBinding

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(R.layout.fragment_report) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setGraphData()
        setGraphSettings()
    }

    private fun setGraphData() {
        val chartEntry = arrayListOf<Entry>()
        val mockList = listOf(20, 30, 50, 90)
        mockList.forEachIndexed { index, listItem ->
            chartEntry.add(Entry(index.toFloat(), listItem.toFloat()))
        }

        binding.chartReport.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("1월", "2월", "3월", "4월"))
            data = LineData(LineDataSet(chartEntry, "").apply {
                color = colorOf(R.color.purple_50)
                circleRadius = 8f
                setCircleColor(colorOf(R.color.purple_50))
                setDrawFilled(true)
                setDrawValues(false)
                lineWidth = 3F
                setDrawCircleHole(false)
                fillColor = colorOf(R.color.purple_10)
                mode = LineDataSet.Mode.LINEAR
            })
            invalidate()
        }
    }

    private fun setGraphSettings() {
        binding.chartReport.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = colorOf(R.color.dark)
                setAvoidFirstLastClipping(true)
            }
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
        }
    }
}