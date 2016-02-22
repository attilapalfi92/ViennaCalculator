package com.attilapalfi.tools.viennacalculator.logic

/**
 * Created by 212461305 on 2016.02.22..
 */
interface AssetFundCalculator {
    fun getResultWithMonthlyPayment(monthlyPayment: Int): Double
    fun getResultWithCustomPayment(monthlyPayment: Int): Double
    fun clear()
}