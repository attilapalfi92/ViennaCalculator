package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.validation.AssetFundValidator
import com.attilapalfi.tools.viennacalculator.logic.validation.ValidationResult
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import javafx.scene.control.DatePicker
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Created by palfi on 2016-03-01.
 */
class InvestmentSimulator(val maxFundCount: Int, val dataIsLoaded: Boolean, val buybackDatePicker: DatePicker,
                          val assetFundHolder: AssetFoundHolder, val defaultCaseByCaseViewHolder: FundViewHolder,
                          val fundViewHolders: List<FundViewHolder>) {

    private val executor = Executors.newFixedThreadPool(maxFundCount)
    private val investmentOutcomes: MutableMap<InvestmentOutcome, Int> = ConcurrentHashMap(2 * maxFundCount)

    fun onSimulation(resultHandler: SimulationResultHandler) {
        if (!dataIsLoaded) {
            // TODO: handle shit
            return
        }
        if (buybackDatePicker.value == null) {
            // TODO: handle shit
            return
        }

        if (defaultCaseByCaseViewHolder.dataIsFilled()) {
            processFilledViewHolder(defaultCaseByCaseViewHolder, assetFundHolder.safeAssetFund)
        }
        fundViewHolders.forEach {
            viewHolder ->
            if (viewHolder.dataIsFilled()) {
                processFilledViewHolder(viewHolder, assetFundHolder.safeAssetFund)
            }
        }
    }

    private fun processFilledViewHolder(filledViewHolder: FundViewHolder, safeAssetFund: AssetFund) {
        val assetFundData = filledViewHolder.getDataHolder()
        val validator = assetFundData.getValidator(safeAssetFund, buybackDatePicker.value)
        handleValidationResult(validator.validate(), validator, assetFundData.monthlyPayment)
    }

    // TODO: handle shits
    private fun handleValidationResult(result: ValidationResult, validator: AssetFundValidator, monthlyPayment: Int) {
        when (result) {
            ValidationResult.VALID -> {
                executor.submit {
                    investmentOutcomes.put(validator.getValidAssetFundCalculator()
                            .getResultWithMonthlyPayment(monthlyPayment).getInvestmentOutcome(), 0)
                }
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