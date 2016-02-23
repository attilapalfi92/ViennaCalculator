package com.attilapalfi.tools.viennacalculator

import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.ui.ApplicationFrame
import org.jfree.ui.RefineryUtilities
import java.awt.Dimension
import java.util.*

/**
 * Created by 212461305 on 2016.02.23..
 */
class TestDiagramVisualizer(title: String, val data: TreeSet<ValueEntry>) : ApplicationFrame(title) {

    val lineChart = ChartFactory.createLineChart(title, "months", "value", createDataset(),
            PlotOrientation.VERTICAL, false, true, false)

    private fun createDataset(): CategoryDataset {
        val dataset = DefaultCategoryDataset()
        data.forEach { dataset.addValue(it.value, "value", it.date) }
        return dataset
    }

    fun showChart(seconds: Long) {
        val chartPanel = ChartPanel(lineChart)
        chartPanel.preferredSize = Dimension(1280, 720)
        contentPane = chartPanel
        pack()
        RefineryUtilities.centerFrameOnScreen(this)
        isVisible = true
        while (true) {
            Thread.sleep(10000)
        }
    }

}