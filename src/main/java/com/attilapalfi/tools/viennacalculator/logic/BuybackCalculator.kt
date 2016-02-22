package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.Inpayment
import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import java.time.LocalDate
import java.util.*

/**
 * Created by 212461305 on 2016.02.22..
 */
class BuybackCalculator(private val inpayments: TreeMap<LocalDate, Inpayment>, private val lastDay: LocalDate,
                        private val lastDaysValue: ValueEntry, private val lastDaysSafeValue: ValueEntry,
                        private val moneyInSafeAsset: Boolean) {

    val twoPercentBorderDay = lastDay.minusYears(1)
    val onePercentBorderDay = lastDay.minusYears(2)

    var totalInpayedForints: Int = 0
    var totalForintsTookOut: Double = 0.0
    var totalBuybackFeeInForints: Double = 0.0

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

    fun getInvestmentOutcome(): InvestmentOutcome {
        resultCache?.let { return it }

        inpayments.forEach { inpayDate, inpayment ->
            totalInpayedForints += inpayment.originalForintAmount

            if (buybackIsFree(inpayDate, onePercentBorderDay)) {
                calculateWithoutFee(inpayment)

            } else if (buybackIsOnePercent(inpayDate, twoPercentBorderDay)) {
                calculateWithBuybackFee(inpayment, 0.01)

            } else {
                calculateWithBuybackFee(inpayment, 0.02)
            }
        }

        val totalMarginInForints = totalForintsTookOut - totalInpayedForints
        val totalYieldWithBuybackFee = (totalForintsTookOut / totalInpayedForints) - 1.0
        val totalYieldWithoutBuybackFee = ((totalForintsTookOut + totalBuybackFeeInForints) / totalInpayedForints) - 1.0

        resultCache = InvestmentOutcome(totalInpayedForints, totalForintsTookOut.toInt(), totalMarginInForints.toInt(),
                totalYieldWithoutBuybackFee, totalYieldWithBuybackFee, totalBuybackFeeInForints.toInt())

        return resultCache as InvestmentOutcome
    }

    private fun calculateWithoutFee(inpayment: Inpayment) {
        val forintsTookOut: Double = inpayment.bondAmount * bondPrice
        totalForintsTookOut += forintsTookOut
    }

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

    private fun buybackIsFree(inpayDate: LocalDate, onePercentBorderDay: LocalDate): Boolean
            = inpayDate.minusYears(2) < onePercentBorderDay

    private fun buybackIsOnePercent(inpayDate: LocalDate, twoPercentBorderDay: LocalDate): Boolean
            = inpayDate.minusYears(1) < twoPercentBorderDay
}