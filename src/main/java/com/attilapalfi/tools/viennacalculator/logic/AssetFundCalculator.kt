package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome

/**
 * Created by 212461305 on 2016.02.22..
 */
interface AssetFundCalculator {
    fun getResultWithMonthlyPayment(monthlyPayment: Int): InvestmentOutcome
    fun getResultWithCustomPayment(monthlyPayment: Int): InvestmentOutcome
    fun clear()
}