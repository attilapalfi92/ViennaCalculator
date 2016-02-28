package com.attilapalfi.tools.viennacalculator.model

/**
 * Created by 212461305 on 2016.02.22..
 */
data class InvestmentOutcome(val totalInpayedForints: Int, val totalForintsTookOutAfterFee: Int,
                             val totalMarginInForintsAfterFee: Int, val totalYieldWithoutBuybackFee: Double,
                             val totalYieldWithBuybackFee: Double, val totalBuybackFeeInForints: Int)