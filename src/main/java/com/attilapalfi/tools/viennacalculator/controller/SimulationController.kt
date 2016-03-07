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
            validateAndCollectCalculators(defaultCaseByCaseViewHolder, safeAssetFund, calculatorList)
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