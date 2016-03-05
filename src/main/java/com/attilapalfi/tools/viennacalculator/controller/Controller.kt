package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.logic.InvestmentSimulatorTask
import com.attilapalfi.tools.viennacalculator.logic.XlsLoaderTask
import com.attilapalfi.tools.viennacalculator.logic.validation.AssetFundValidator
import com.attilapalfi.tools.viennacalculator.logic.validation.ValidationResult
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import com.attilapalfi.tools.viennacalculator.view.RestrictingDateCell
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class Controller : Initializable {

    var stage: Stage? = null

    private val maxFundCount = 10
    private var fundCount: Int = 1

    private val executor = Executors.newFixedThreadPool(1)
    private val fundViewHolders: MutableList<FundViewHolder> = ArrayList()
    private lateinit var defaultCaseByCaseViewHolder: FundViewHolder

    private var xlsLoaderTask: XlsLoaderTask? = null
    private var assetFundHolder: AssetFoundHolder? = null
    @Volatile
    private var dataIsLoaded: Boolean = false
    private val investmentOutcomes: MutableMap<InvestmentOutcome, Int> = ConcurrentHashMap(2 * maxFundCount)

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
    lateinit var buybackDatePicker: DatePicker

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

    private fun fillChoiceBoxes(assetFundHolder: AssetFoundHolder) {
        mandatoryFeeAssetFundChoiceBox.items.addAll(assetFundHolder.assetFunds)
        mandatoryFeeAssetFundChoiceBox.selectionModel.select(0)
        caseByCaseAssetFundChoiceBox.items.addAll(assetFundHolder.assetFunds)
        caseByCaseAssetFundChoiceBox.selectionModel.select(0)
        fundViewHolders.forEach { holder ->
            holder.assetFundChoiceBox?.items?.addAll(assetFundHolder.assetFunds)
            holder.assetFundChoiceBox?.selectionModel?.select(0)
        }
    }

    @FXML
    private fun onAddNewFund(event: ActionEvent) {
        if (fundCount < maxFundCount) {
            val builder = FundViewBuilder(fundContainer)
            val viewHolder = builder.build(assetFundHolder)
            fundViewHolders.add(viewHolder)
            fundCount++
        }
    }

    @FXML
    private fun onSimulation(event: ActionEvent) {
        if (dataAndInputIsInadequate()) {
            return
        }
        tryToSimulate()
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
        return false
    }

    private fun tryToSimulate() {
        val safeAssetFund = (assetFundHolder as AssetFoundHolder).safeAssetFund
        val calculatorList = initListByDefaultFundViewHolder(safeAssetFund)
        addOtherFundViewHolders(calculatorList, safeAssetFund)
        val simulatorTask = InvestmentSimulatorTask(calculatorList, maxFundCount)
        simulatorTask.setOnSucceeded { showResultOnGui(simulatorTask.get()) }
        executor.submit(simulatorTask)
    }

    private fun showResultOnGui(investmentOutcome: InvestmentOutcome) {
        totalInpaymentsText.text = investmentOutcome.totalInpayedForints.toString()
        totalForintsTookOutAfterFeeText.text = investmentOutcome.totalForintsTookOutAfterFee.toString()
        totalMarginInForintsAfterFeeText.text = investmentOutcome.totalMarginInForintsAfterFee.toString()
        totalYieldWithBuybackFeeText.text = investmentOutcome.totalYieldWithBuybackFee.toString()
        totalBuybackFeeInForintsText.text = investmentOutcome.totalBuybackFeeInForints.toString()

    }

    private fun initListByDefaultFundViewHolder(safeAssetFund: AssetFund): MutableList<AssetFundCalculator> {
        val calculatorList = ArrayList<AssetFundCalculator>()
        if (defaultCaseByCaseViewHolder.dataIsFilled()) {
            validateAndCollectCalculators(defaultCaseByCaseViewHolder, safeAssetFund, calculatorList)
        }
        return calculatorList
    }

    private fun validateAndCollectCalculators(filledViewHolder: FundViewHolder, safeAssetFund: AssetFund,
                                              calculatorList: MutableList<AssetFundCalculator>) {
        val assetFundData = filledViewHolder.getDataHolder()
        val validator = assetFundData.getValidator(safeAssetFund, buybackDatePicker.value)
        handleValidationResult(validator, calculatorList)
    }

    private fun addOtherFundViewHolders(calculatorList: MutableList<AssetFundCalculator>, safeAssetFund: AssetFund) {
        fundViewHolders.forEach {
            viewHolder ->
            if (viewHolder.dataIsFilled()) {
                validateAndCollectCalculators(viewHolder, safeAssetFund, calculatorList)
            }
        }
    }

    // TODO: handle shits
    private fun handleValidationResult(validator: AssetFundValidator, calculatorList: MutableList<AssetFundCalculator>) {
        when (validator.validate()) {
            ValidationResult.VALID -> {
                calculatorList.add(validator.getValidAssetFundCalculator())
            }
            ValidationResult.OUT_OF_RANGE_PAY_START_DATE -> {

            }
            ValidationResult.OUT_OF_RANGE_PAY_END_DATE -> {

            }
            ValidationResult.OUT_OF_RANGE_BUYBACK_DATE -> {

            }
            ValidationResult.START_LATER_THAN_END -> {

            }
            ValidationResult.START_LATER_THAN_BUYBACK -> {

            }
            ValidationResult.END_LATER_THAN_BUYBACK -> {

            }
            ValidationResult.UNVALIDATED -> {

            }
        }
    }
}
