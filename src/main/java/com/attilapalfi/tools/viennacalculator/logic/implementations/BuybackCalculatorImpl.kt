package com.attilapalfi.tools.viennacalculator.logic.implementations

import com.attilapalfi.tools.viennacalculator.logic.BuybackCalculator
import com.attilapalfi.tools.viennacalculator.model.Inpayment
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import java.time.LocalDate
import java.util.*

/**
 * Created by 212461305 on 2016.02.22..
 */
class BuybackCalculatorImpl(private val inpayments: TreeMap<LocalDate, Inpayment>, val lastDay: LocalDate,
                            val lastDaysValue: ValueEntry, val lastDaysSafeValue: ValueEntry,
                            val moneyInSafeAsset: Boolean) : BuybackCalculator {

    val publishedInpayments: Map<LocalDate, Inpayment> = inpayments

    val twoPercentBorderDay = lastDay.minusYears(1)
    val onePercentBorderDay = lastDay.minusYears(2)

    var totalInpayedForints: Int = 0
    var totalForintsTookOut: Double = 0.0
    var totalBuybackFeeInForints: Double = 0.0
    var totalMarginInForints: Double = 0.0
    var totalYieldWithBuybackFee: Double = 0.0
    var totalYieldWithoutBuybackFee: Double = 0.0

    val bondPrice: Double = if (!moneyInSafeAsset) {
        lastDaysValue.value
    } else {
        lastDaysSafeValue.value
    }

    var forintsTookOutWithoutFee: Double = 0.0
    var yieldWithoutBuybackFee: Double = 0.0
    var yieldWithBuybackFee: Double = 0.0
    var forintsTookOutWithFee: Double = 0.0
    var buybackFeeInForints: Double = 0.0

    var resultCache: InvestmentOutcome? = null

    override fun getInvestmentOutcome(): InvestmentOutcome {
        resultCache?.let { return it }
        inpayments.forEach { inpayDate, inpayment ->
            processInpayment(inpayDate, inpayment)
        }
        calculateYieldAndMargin()
        cacheOutcome()
        return resultCache as InvestmentOutcome
    }

    private fun processInpayment(inpayDate: LocalDate, inpayment: Inpayment) {
        totalInpayedForints += inpayment.originalForintAmount

        if (buybackIsFree(inpayDate)) {
            calculateWithoutFee(inpayment)

        } else if (buybackIsOnePercent(inpayDate)) {
            calculateWithBuybackFee(inpayment, 0.01)

        } else {
            calculateWithBuybackFee(inpayment, 0.02)
        }
    }

    private fun buybackIsFree(inpayDate: LocalDate): Boolean
            = inpayDate < onePercentBorderDay

    private fun calculateWithoutFee(inpayment: Inpayment) {
        val forintsTookOut: Double = inpayment.bondAmount * bondPrice
        totalForintsTookOut += forintsTookOut
    }

    private fun buybackIsOnePercent(inpayDate: LocalDate): Boolean
            = inpayDate < twoPercentBorderDay

    private fun calculateWithBuybackFee(inpayment: Inpayment, fee: Double) {
        forintsTookOutWithoutFee = inpayment.bondAmount * bondPrice
        yieldWithoutBuybackFee = yieldWithoutFee(inpayment)
        yieldWithBuybackFee = yieldWithFee(fee)
        forintsTookOutWithFee = tookOutForintsWithFee(inpayment)
        totalForintsTookOut += forintsTookOutWithFee
        buybackFeeInForints = buybackFeeInForints()
        totalBuybackFeeInForints += buybackFeeInForints
    }

    private fun yieldWithoutFee(inpayment: Inpayment): Double {
        val marginInForintsWithoutFee = forintsTookOutWithoutFee - inpayment.originalForintAmount
        val yieldWithoutBuybackFee = marginInForintsWithoutFee / inpayment.originalForintAmount
        return yieldWithoutBuybackFee
    }

    private fun yieldWithFee(fee: Double): Double = yieldWithoutBuybackFee - fee

    private fun tookOutForintsWithFee(inpayment: Inpayment): Double
            = (yieldWithBuybackFee + 1.0) * inpayment.originalForintAmount

    private fun buybackFeeInForints(): Double
            = forintsTookOutWithoutFee - forintsTookOutWithFee

    private fun calculateYieldAndMargin() {
        totalMarginInForints = totalForintsTookOut - totalInpayedForints
        totalYieldWithBuybackFee = (totalForintsTookOut / totalInpayedForints) - 1.0
        totalYieldWithoutBuybackFee = ((totalForintsTookOut + totalBuybackFeeInForints) / totalInpayedForints) - 1.0
    }

    private fun cacheOutcome() {
        resultCache = InvestmentOutcome(totalInpayedForints, totalForintsTookOut.toInt(), totalMarginInForints.toInt(),
                totalYieldWithoutBuybackFee, totalYieldWithBuybackFee, totalBuybackFeeInForints.toInt())
    }
}