package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.XlsLoaderTask
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ProgressBar
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.util.concurrent.Executors

class Controller {

    var stage: Stage? = null
    private val executor = Executors.newFixedThreadPool(2)
    private var xlsLoaderTask: XlsLoaderTask? = null
    private var assetFundHolder: AssetFoundHolder? = null

    @FXML
    lateinit var loadHistoricalDataBtn: Button
    @FXML
    lateinit var loadProgressBar: ProgressBar
    @FXML
    lateinit var mandatoryFeeAssetFundChoiceBox: ChoiceBox<AssetFund>

    @FXML
    private fun onLoadHistoricalData(event: ActionEvent) {
        val fileChooser = createFileChooser()
        val file = fileChooser.showOpenDialog(stage)
        file?.let {
            xlsLoaderTask = XlsLoaderTask(it)
            onLoadSucceed()
            loadProgressBar.progressProperty().bind(xlsLoaderTask?.progressProperty())
            executor.submit(xlsLoaderTask)
        }
    }

    private fun onLoadSucceed() {
        xlsLoaderTask?.setOnSucceeded { event ->
            assetFundHolder = xlsLoaderTask?.get()
            assetFundHolder?.let {
                mandatoryFeeAssetFundChoiceBox.items.addAll(it.assetFunds)
                mandatoryFeeAssetFundChoiceBox.selectionModel.select(0)
            }
        }
    }

    private fun createFileChooser(): FileChooser {
        val fileChooser = FileChooser();
        fileChooser.title = "Historikus adatfájl megnyitása"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls"))
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"))
        return fileChooser
    }
}
