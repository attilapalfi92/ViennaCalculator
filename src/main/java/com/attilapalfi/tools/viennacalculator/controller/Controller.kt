package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.XlsLoaderTask
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class Controller : Initializable {

    var stage: Stage? = null

    private val executor = Executors.newFixedThreadPool(2)
    private val fundViewHolders: MutableList<FundViewHolder> = ArrayList()
    private lateinit var defaultCaseByCaseViewHolder: FundViewHolder

    private var xlsLoaderTask: XlsLoaderTask? = null
    private var assetFundHolder: AssetFoundHolder? = null

    @FXML
    lateinit var loadProgressBar: ProgressBar
    @FXML
    lateinit var fundContainer: VBox

    @FXML
    lateinit var mandatoryFeeStartDate: DatePicker
    @FXML
    lateinit var mandatoryFeeEndDate: DatePicker
    @FXML
    lateinit var mandatoryPaymentText: TextField
    @FXML
    lateinit var mandatoryFeeAssetFundChoiceBox: ChoiceBox<AssetFund>
    @FXML
    lateinit var mandatoryPaymentRateMonitoringCheckBox: CheckBox

    @FXML
    lateinit var caseByCasePaymentStartDate: DatePicker
    @FXML
    lateinit var caseByCasePaymentEndDate: DatePicker
    @FXML
    lateinit var caseByCasePaymentText: TextField
    @FXML
    lateinit var caseByCaseAssetFundChoiceBox: ChoiceBox<AssetFund>
    @FXML
    lateinit var caseByCasePaymentRateMonitoringCheckBox: CheckBox

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        defaultCaseByCaseViewHolder = FundViewHolder(
                paymentStartDate = caseByCasePaymentStartDate,
                paymentEndDate = caseByCasePaymentEndDate,
                monthlyPaymentText = caseByCasePaymentText,
                assetFundChoiceBox = caseByCaseAssetFundChoiceBox,
                paymentRateMonitoringCheckBox = caseByCasePaymentRateMonitoringCheckBox
        )
    }

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

    private fun createFileChooser(): FileChooser {
        val fileChooser = FileChooser();
        fileChooser.title = "Historikus adatfájl megnyitása"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls"))
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"))
        return fileChooser
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

    @FXML
    private fun onAddNewFund(event: ActionEvent) {
        val builder = FundViewBuilder(fundContainer)
        val viewHolder = builder.build()
        fundViewHolders.add(viewHolder)
    }

    @FXML
    private fun onSimulation(event: ActionEvent) {

    }
}
