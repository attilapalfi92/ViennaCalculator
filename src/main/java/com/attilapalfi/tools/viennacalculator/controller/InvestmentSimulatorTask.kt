package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.validation.AssetFundValidator
import com.attilapalfi.tools.viennacalculator.logic.validation.ValidationResult
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import javafx.concurrent.Task
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Created by palfi on 2016-03-01.
 */
class InvestmentSimulatorTask(val maxFundCount: Int, val dataIsLoaded: Boolean, val buybackDate: LocalDate,
                              val assetFundHolder: AssetFoundHolder, val defaultCaseByCaseViewHolder: FundViewHolder,
                              val fundViewHolders: List<FundViewHolder>) : Task<InvestmentOutcome>() {

    private val executor = Executors.newFixedThreadPool(maxFundCount)
    private val investmentOutcomes: MutableMap<InvestmentOutcome, Int> = ConcurrentHashMap(2 * maxFundCount)

    override fun call(): InvestmentOutcome? {
        if (defaultCaseByCaseViewHolder.dataIsFilled()) {
            processFilledViewHolder(defaultCaseByCaseViewHolder, assetFundHolder.safeAssetFund)
        }
        fundViewHolders.forEach {
            viewHolder ->
            if (viewHolder.dataIsFilled()) {
                processFilledViewHolder(viewHolder, assetFundHolder.safeAssetFund)
            }
        }
        return InvestmentOutcome(0, 0, 0, 0.0, 0.0, 0)
    }

    private fun processFilledViewHolder(filledViewHolder: FundViewHolder, safeAssetFund: AssetFund) {
        val assetFundData = filledViewHolder.getDataHolder()
        val validator = assetFundData.getValidator(safeAssetFund, buybackDate)
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