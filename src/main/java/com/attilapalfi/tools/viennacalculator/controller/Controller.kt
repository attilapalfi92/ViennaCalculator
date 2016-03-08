package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.exception.InvalidInputsException
import com.attilapalfi.tools.viennacalculator.logic.XlsLoaderTask
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.AssetFundHolder
import com.attilapalfi.tools.viennacalculator.view.AssetFundChartView
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import com.attilapalfi.tools.viennacalculator.view.RestrictingDateCell
import com.attilapalfi.tools.viennacalculator.view.ResultSummaryViewHolder
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class Controller : Initializable {

    var stage: Stage? = null

    private val maxFundCount = 10
    private var fundCount: Int = 1

    private val executor = Executors.newFixedThreadPool(1)
    private val addedFundViewHolders: MutableList<FundViewHolder> = ArrayList()

    private var xlsLoaderTask: XlsLoaderTask? = null
    private var assetFundHolder: AssetFundHolder? = null
    @Volatile
    private var dataIsLoaded: Boolean = false

    @FXML
    lateinit var loadProgressBar: ProgressBar
    @FXML
    lateinit var fundContainer: VBox


    private lateinit var mandatoryViewHolder: FundViewHolder
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
    lateinit var mandatoryShowDiagramButton: Button


    private lateinit var defaultCaseByCaseViewHolder: FundViewHolder
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
    @FXML
    lateinit var caseByCaseShowDiagramButton: Button


    private lateinit var resultSummaryViewHolder: ResultSummaryViewHolder
    @FXML
    lateinit var buybackDatePicker: DatePicker
    @FXML
    lateinit var totalInpaymentsText: TextField
    @FXML
    lateinit var totalForintsTookOutAfterFeeText: TextField
    @FXML
    lateinit var totalMarginInForintsAfterFeeText: TextField
    @FXML
    lateinit var totalYieldWithBuybackFeeText: TextField
    @FXML
    lateinit var totalBuybackFeeInForintsText: TextField


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mandatoryFeeStartDate.dayCellFactory = Callback {
            RestrictingDateCell(mandatoryFeeAssetFundChoiceBox)
        }
        mandatoryFeeEndDate.dayCellFactory = Callback {
            RestrictingDateCell(mandatoryFeeAssetFundChoiceBox)
        }
        caseByCasePaymentStartDate.dayCellFactory = Callback {
            RestrictingDateCell(caseByCaseAssetFundChoiceBox)
        }
        caseByCasePaymentEndDate.dayCellFactory = Callback {
            RestrictingDateCell(caseByCaseAssetFundChoiceBox)
        }
        defaultCaseByCaseViewHolder = FundViewHolder(
                paymentStartDate = caseByCasePaymentStartDate,
                paymentEndDate = caseByCasePaymentEndDate,
                monthlyPaymentText = caseByCasePaymentText,
                assetFundChoiceBox = caseByCaseAssetFundChoiceBox,
                paymentRateMonitoringCheckBox = caseByCasePaymentRateMonitoringCheckBox
        )
        mandatoryViewHolder = FundViewHolder(
                paymentStartDate = mandatoryFeeStartDate,
                paymentEndDate = mandatoryFeeEndDate,
                monthlyPaymentText = mandatoryPaymentText,
                assetFundChoiceBox = mandatoryFeeAssetFundChoiceBox,
                paymentRateMonitoringCheckBox = mandatoryPaymentRateMonitoringCheckBox
        )
        resultSummaryViewHolder = ResultSummaryViewHolder(
                totalInpaymentsText = totalInpaymentsText,
                totalBuybackFeeInForintsText = totalBuybackFeeInForintsText,
                totalForintsTookOutAfterFeeText = totalForintsTookOutAfterFeeText,
                totalMarginInForintsAfterFeeText = totalMarginInForintsAfterFeeText,
                totalYieldWithBuybackFeeText = totalYieldWithBuybackFeeText
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
        xlsLoaderTask?.setOnSucceeded {
            assetFundHolder = xlsLoaderTask?.get()
            assetFundHolder?.let {
                fillChoiceBoxes(it)
                dataIsLoaded = true
            }
        }
    }

    private fun fillChoiceBoxes(assetFundHolder: AssetFundHolder) {
        mandatoryFeeAssetFundChoiceBox.items.addAll(assetFundHolder.assetFunds)
        mandatoryFeeAssetFundChoiceBox.selectionModel.select(0)
        mandatoryShowDiagramButton.isDisable = false

        caseByCaseAssetFundChoiceBox.items.addAll(assetFundHolder.assetFunds)
        caseByCaseAssetFundChoiceBox.selectionModel.select(0)
        caseByCaseShowDiagramButton.isDisable = false

        addedFundViewHolders.forEach { holder ->
            holder.assetFundChoiceBox?.items?.addAll(assetFundHolder.assetFunds)
            holder.assetFundChoiceBox?.selectionModel?.select(0)
            holder.showDiagramButton?.isDisable = false
        }
    }

    @FXML
    private fun onAddNewFund(event: ActionEvent) {
        if (fundCount < maxFundCount) {
            val builder = FundViewBuilder(fundContainer)
            val viewHolder = builder.build(assetFundHolder)
            addedFundViewHolders.add(viewHolder)
            fundCount++
        }
    }

    @FXML
    private fun onSimulation(event: ActionEvent) {
        if (dataAndInputIsInadequate()) {
            return
        }
        val simulator = SimulationController(
                executor = executor,
                maxFundCount = maxFundCount,
                assetFundHolder = assetFundHolder as AssetFundHolder,
                mandatoryViewHolder = mandatoryViewHolder,
                addedFundViewHolders = addedFundViewHolders,
                buybackDatePicker = buybackDatePicker,
                defaultCaseByCaseViewHolder = defaultCaseByCaseViewHolder,
                summaryHolder = resultSummaryViewHolder)
        try {
            simulator.tryToSimulate()
        } catch (e: InvalidInputsException) {
            // TODO: handle shit
        }
    }

    @FXML
    private fun onMandatoryShowDiagramClick(event: ActionEvent) {
        showDiagram(mandatoryFeeAssetFundChoiceBox)
    }

    @FXML
    private fun onCaseByCaseShowDiagramClick(event: ActionEvent) {
        showDiagram(caseByCaseAssetFundChoiceBox)
    }

    private fun showDiagram(choiceBox: ChoiceBox<AssetFund>) {
        if ( choiceBox.value != null ) {
            AssetFundChartView.show(choiceBox.value)
        } else {
            // TODO: handle shit
        }
    }

    private fun dataAndInputIsInadequate(): Boolean {
        if (!dataIsLoaded) {
            // TODO: handle shit
            return true
        }
        if (buybackDatePicker.value == null) {
            // TODO: handle shit
            return true
        }
        if (assetFundHolder == null) {
            // TODO: handle shit
            return true
        }
        if (!mandatoryViewHolder.dataIsFilled()) {
            // TODO: handle shit
            return true
        }
        return false
    }
}
