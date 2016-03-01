package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import javafx.concurrent.Task
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by palfi on 2016-03-01.
 */
class InvestmentSimulatorTask(val calculatorList: MutableList<AssetFundCalculator>,
                              val maxFundCount: Int) : Task<InvestmentOutcome>() {

    private val executor = Executors.newFixedThreadPool(maxFundCount.coerceAtMost(Runtime.getRuntime().availableProcessors()))
    private lateinit var resultOutcome: InvestmentOutcome

    override fun call(): InvestmentOutcome {
        val futureList: List<Future<InvestmentOutcome>> = calculatorList.map {
            executor.submit(Callable<InvestmentOutcome> {
                it.getBuybackCalculator().getInvestmentOutcome()
            })
        }
        val investmentOutcomes: List<InvestmentOutcome> = futureList.map { it.get() }
        return mergeOutcomes(investmentOutcomes)
    }

    private fun mergeOutcomes(outcomes: List<InvestmentOutcome>): InvestmentOutcome {
        val totalInpayedForints = outcomes.sumBy { it.totalInpayedForints }
        val totalMarginInForintsAfterFee = outcomes.sumBy { it.totalMarginInForintsAfterFee }
        val totalBuybackFeeInForints = outcomes.sumBy { it.totalBuybackFeeInForints }

        resultOutcome = InvestmentOutcome(
                totalInpayedForints = totalInpayedForints,
                totalForintsTookOutAfterFee = outcomes.sumBy { it.totalForintsTookOutAfterFee },
                totalMarginInForintsAfterFee = totalMarginInForintsAfterFee,
                totalBuybackFeeInForints = totalBuybackFeeInForints,
                totalYieldWithoutBuybackFee = (totalMarginInForintsAfterFee + totalBuybackFeeInForints)
                        .toDouble() / totalInpayedForints,
                totalYieldWithBuybackFee = totalMarginInForintsAfterFee.toDouble() / totalInpayedForints
        )
        succeeded()
        return resultOutcome
    }
}