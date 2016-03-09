package com.attilapalfi.tools.viennacalculator.view

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.chart.AreaChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.stage.Stage

/**
 * Created by 212461305 on 2016.03.07..
 */
object AssetFundChartView {

    fun show(assetFund: AssetFund?) {
        if (assetFund != null) {
            val chart = getChart(assetFund)
            val series = getSeries(assetFund)
            chart.data.add(series)
            showStage(assetFund, chart)
        } else {
            // TODO: HANDLE SHIT
        }
    }

    private fun getChart(assetFund: AssetFund): AreaChart<String, Number> {
        val xAxis = CategoryAxis().apply { label = "Napok" }
        val yAxis = NumberAxis().apply { label = "Érték" }
        val chart = AreaChart<String, Number>(xAxis, yAxis)
                .apply { title = assetFund.name; createSymbols = false }
        return chart
    }

    private fun getSeries(assetFund: AssetFund): XYChart.Series<String, Number> {
        val series = XYChart.Series<String, Number>().apply { name = assetFund.name }
        setColor(series)
        assetFund.valueHistory.forEach {
            val data = XYChart.Data(it.date.toString(), it.value as Number)
            series.data.add(data)
            Tooltip.install(data.node, Tooltip("${data.yValue}\n${data.xValue}"))
        }
        return series
    }

    private fun setColor(series: XYChart.Series<String, Number>) {
        //setLineColor(series)
        //setFillColor(series)
    }

    private fun setLineColor(series: XYChart.Series<String, Number>) {
        val line: Node = series.node.lookup(".chart-series-area-line")
        val lineColor = colorToString(Color.AZURE)
        line.style = "-fx-stroke: rgba($lineColor, 1.0);"
    }

    private fun setFillColor(series: XYChart.Series<String, Number>) {
        val fill: Node = series.node.lookup(".chart-series-area-fill")
        val fillColor = colorToString(Color.AQUA)
        fill.style = "-fx-fill: rgba($fillColor, 0.15);";
    }

    private fun colorToString(color: Color): String
            = "${color.red * 255}, ${color.green * 255}, ${color.blue * 255}"

    private fun showStage(assetFund: AssetFund, chart: AreaChart<String, Number>) {
        val stage = Stage()
        stage.title = "${assetFund.name} diagram"
        stage.scene = Scene(chart)
        stage.show()
    }
}