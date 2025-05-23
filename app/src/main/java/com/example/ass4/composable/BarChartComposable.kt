package com.example.ass4.components

import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ass4.database.MonthlyRepeatReusage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun BarChartCard(
    data: List<MonthlyRepeatReusage>,
    selectedMonth: String?,
    onBarSelected: (String?) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setFitBars(true)
                animateY(1000)

                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                legend.isEnabled = false

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    labelRotationAngle = -30f
                }

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        val index = e?.x?.toInt() ?: return
                        if (index in data.indices) {
                            onBarSelected(data[index].month)
                        }
                    }

                    override fun onNothingSelected() {
                        onBarSelected(null)
                    }
                })

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        onBarSelected(null)
                    }
                    false
                }
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.repeat_count.toFloat())
            }

            val labels = data.map { it.month }

            val barColors = entries.mapIndexed { index, _ ->
                if (selectedMonth == null || labels[index] == selectedMonth) {
                    ColorTemplate.COLORFUL_COLORS[index % ColorTemplate.COLORFUL_COLORS.size]
                } else {
                    android.graphics.Color.argb(80, 180, 180, 180) // 半透明灰色
                }
            }

            val dataSet = BarDataSet(entries, "Monthly Wear Count").apply {
                colors = barColors
                setDrawValues(true)
                valueTextSize = 12f
                valueTextColor = android.graphics.Color.BLACK
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.9f
            }

            chart.data = barData
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            // 设置高亮
            if (selectedMonth != null) {
                val index = labels.indexOf(selectedMonth)
                if (index != -1) {
                    chart.highlightValue(index.toFloat(), 0)
                }
            } else {
                chart.highlightValue(null)
            }

            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
