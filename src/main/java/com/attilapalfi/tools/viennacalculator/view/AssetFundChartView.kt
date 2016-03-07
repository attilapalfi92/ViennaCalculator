package com.attilapalfi.tools.viennacalculator.view

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage

/**
 * Created by 212461305 on 2016.03.07..
 */
object AssetFundChartView {

    fun show(assetFund: AssetFund) {
        val xAxis = NumberAxis().apply { label = "Napok" }
        val yAxis = NumberAxis().apply { label = "Érték" }

        val chart = LineChart<Number, Number>(xAxis, yAxis).apply { setTitle(assetFund.name) }
        val series = XYChart.Series<Number, Number>().apply { setName(assetFund.name) }

        assetFund.valueHistory.forEachIndexed { i, valueEntry ->
            series.data.add(XYChart.Data(i, valueEntry.value))
        }

        chart.data.add(series)
        chart.createSymbols = false
        val scene = Scene(chart)//, 800.0, 600.0)

        val stage = Stage()
        stage.title = "${assetFund.name} diagram"
        stage.scene = scene

        stage.show()
    }
}