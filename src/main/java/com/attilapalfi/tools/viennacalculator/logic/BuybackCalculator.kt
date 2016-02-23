package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.InvestmentOutcome

/**
 * Created by 212461305 on 2016.02.23..
 */
interface BuybackCalculator {
    fun getInvestmentOutcome(): InvestmentOutcome
}