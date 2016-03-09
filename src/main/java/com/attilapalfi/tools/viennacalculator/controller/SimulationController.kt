package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.exception.InvalidInputsException
import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.logic.InvestmentSimulatorTask
import com.attilapalfi.tools.viennacalculator.logic.validation.AssetFundValidator
import com.attilapalfi.tools.viennacalculator.logic.validation.ValidationResult
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.AssetFundHolder
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import com.attilapalfi.tools.viennacalculator.view.ResultSummaryViewHolder
import javafx.scene.control.Alert
import javafx.scene.control.DatePicker
import java.util.*
import java.util.concurrent.ExecutorService

/**
 * Created by palfi on 2016-03-06.
 */
class SimulationController(private val executor: ExecutorService,
                           private val maxFundCount: Int,
                           private val assetFundHolder: AssetFundHolder,
                           private val mandatoryViewHolder: FundViewHolder,
                           private val defaultCaseByCaseViewHolder: FundViewHolder,
                           private val addedFundViewHolders: List<FundViewHolder>,
                           private val buybackDatePicker: DatePicker,
                           private val summaryHolder: ResultSummaryViewHolder) {

    fun tryToSimulate() {
        val safeAssetFund = assetFundHolder.safeAssetFund
        val calculatorList = initListWithMandatoryViewHolder(safeAssetFund)
        adDefaultFundViewHolderToList(calculatorList, safeAssetFund)
        addOtherFundViewHolders(calculatorList, safeAssetFund)
        val simulatorTask = InvestmentSimulatorTask(calculatorList, maxFundCount)
        simulatorTask.setOnSucceeded { showResultOnGui(simulatorTask.get()) }
        executor.submit(simulatorTask)
    }

    private fun initListWithMandatoryViewHolder(safeAssetFund: AssetFund): MutableList<AssetFundCalculator> {
        val calculatorList = ArrayList<AssetFundCalculator>()
        if (mandatoryViewHolder.dataIsFilled()) {
            validateAndCollectCalculators(mandatoryViewHolder, safeAssetFund, calculatorList)
        } else {
            throw InvalidInputsException()
        }
        return calculatorList
    }

    private fun adDefaultFundViewHolderToList(calculatorList: MutableList<AssetFundCalculator>, safeAssetFund: AssetFund) {
        if (defaultCaseByCaseViewHolder.dataIsFilled()) {
            validateAndCollectCalculators(defaultCaseByCaseViewHolder, safeAssetFund, calculatorList)
        }
    }

    private fun validateAndCollectCalculators(filledViewHolder: FundViewHolder, safeAssetFund: AssetFund,
                                              calculatorList: MutableList<AssetFundCalculator>) {
        val assetFundData = filledViewHolder.getDataHolder()
        val validator = assetFundData.getValidator(safeAssetFund, buybackDatePicker.value)
        handleValidationResult(validator, calculatorList)
    }

    private fun addOtherFundViewHolders(calculatorList: MutableList<AssetFundCalculator>, safeAssetFund: AssetFund) {
        addedFundViewHolders.forEach {
            viewHolder ->
            if (viewHolder.dataIsFilled()) {
                validateAndCollectCalculators(viewHolder, safeAssetFund, calculatorList)
            } else {

            }
        }
    }

    private fun showResultOnGui(investmentOutcome: InvestmentOutcome) {
        summaryHolder.totalInpaymentsText.text = investmentOutcome.totalInpayedForints.toString()
        summaryHolder.totalForintsTookOutAfterFeeText.text = investmentOutcome.totalForintsTookOutAfterFee.toString()
        summaryHolder.totalMarginInForintsAfterFeeText.text = investmentOutcome.totalMarginInForintsAfterFee.toString()
        summaryHolder.totalYieldWithBuybackFeeText.text = investmentOutcome.totalYieldWithBuybackFee.toString()
        summaryHolder.totalBuybackFeeInForintsText.text = investmentOutcome.totalBuybackFeeInForints.toString()
    }

    private fun handleValidationResult(validator: AssetFundValidator, calculatorList: MutableList<AssetFundCalculator>) {
        when (validator.validate()) {
            ValidationResult.VALID -> {
                calculatorList.add(validator.getValidAssetFundCalculator())
            }
            ValidationResult.OUT_OF_RANGE_PAY_START_DATE -> {
                outOfRangePayStartDate(validator)
            }
            ValidationResult.OUT_OF_RANGE_PAY_END_DATE -> {
                outOfRangePayEndDate(validator)
            }
            ValidationResult.OUT_OF_RANGE_BUYBACK_DATE -> {
                outOfRangeBuybackDate(validator)
            }
            ValidationResult.START_LATER_THAN_END -> {
                startLaterThanEnd(validator)
            }
            ValidationResult.START_LATER_THAN_BUYBACK -> {
                startLaterThanBuyback(validator)
            }
            ValidationResult.END_LATER_THAN_BUYBACK -> {
                endLaterThanBuyback(validator)
            }
            else -> {
            }
        }
    }

    private fun startLaterThanEnd(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "${validator.assetFund.name}: Hibás a befizetés kezdete és vége"
            headerText = "Hibás a befizetés kezdete és vége."
            contentText = "Állítsa be, hogy a befizetés kezdete a vége előtt legyen."
        }
        alert.showAndWait()
    }

    private fun startLaterThanBuyback(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "${validator.assetFund.name}: Hibás a befizetés kezdete és a visszavásárlási idő"
            headerText = "Hibás a befizetés kezdete és a visszavásárlási idő."
            contentText = "Állítsa be, hogy a befizetés kezdete a visszavásárlási idő előtt legyen."
        }
        alert.showAndWait()
    }

    private fun endLaterThanBuyback(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "${validator.assetFund.name}: Hibás a befizetés vége és a visszavásárlási idő"
            headerText = "Hibás a befizetés vége és a visszavásárlási idő."
            contentText = "Állítsa be, hogy a befizetés vége a visszavásárlási idő előtt legyen."
        }
        alert.showAndWait()
    }

    private fun outOfRangePayStartDate(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "Hibás a befizetés kezdete"
            headerText = "Állítson be helyes időpontot a befizetés kezdetének."
            contentText = "Befizetés kezdete: ${validator.payStartDate}" +
                    getFundTimeIntervalString(validator)
        }
        alert.showAndWait()
    }

    private fun outOfRangePayEndDate(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "Hibás a befizetés vége"
            headerText = "Állítson be helyes időpontot a befizetés végének."
            contentText = "Befizetés vége: ${validator.payEndDate}" +
                    getFundTimeIntervalString(validator)
        }
        alert.showAndWait()
    }

    private fun outOfRangeBuybackDate(validator: AssetFundValidator) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            title = "Hibás visszavásárlási idő"
            headerText = "Állítson be helyes visszavásárlási időpontot."
            contentText = "Visszavásárlási idő: ${validator.buybackDate}\n" +
                    getFundTimeIntervalString(validator)
        }
        alert.showAndWait()
    }

    private fun getFundTimeIntervalString(validator: AssetFundValidator): String =
            "${validator.assetFund.name} értékei az alábbi időintervallumban ismertek:\n" +
                    "${validator.assetFund.valueHistory.first().date} - " +
                    "${validator.assetFund.valueHistory.last().date}\n" +
                    "${validator.safeAssetFund.name} értékei az alábbi időintervallumban ismertek:\n" +
                    "${validator.safeAssetFund.valueHistory.first().date} - " +
                    "${validator.safeAssetFund.valueHistory.last().date}"
}