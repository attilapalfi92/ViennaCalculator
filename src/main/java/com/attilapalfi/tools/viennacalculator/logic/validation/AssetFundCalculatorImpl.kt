package com.attilapalfi.tools.viennacalculator.logic.validation

import com.attilapalfi.tools.viennacalculator.logic.AbstractAssetFundWorker
import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.Inpayment
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import java.time.LocalDate
import java.util.*

/**
 * Created by palfi on 2016-02-21.
 */
/*
assetFund: AssetFund, safeAssetFund: AssetFund,
                              payStartDate: LocalDate, payEndDate: LocalDate,
                              buybackDate: LocalDate

assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate
 */

class AssetFundCalculatorImpl : AssetFundCalculator,
        AbstractAssetFundWorker {

    constructor(assetFund: AssetFund, safeAssetFund: AssetFund,
                payStartDate: LocalDate, payEndDate: LocalDate,
                buybackDate: LocalDate) : super(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate)

    private val purchaseDays: Int = if (payStartDate.dayOfMonth > 28) {
        28
    } else {
        payStartDate.dayOfMonth
    }
    private var moneyInSafeAsset: Boolean = false
    private val inpayments = TreeMap<LocalDate, Inpayment>()

    private val trimmedAssetFund: List<ValueEntry>
            by lazy { assetFund.valueHistory.filter { it.date >= payStartDate || it.date <= buybackDate }.sorted() }

    private val trimmedSafeAssetFund: List<ValueEntry>
            by lazy { safeAssetFund.valueHistory.filter { it.date >= payStartDate || it.date <= buybackDate }.sorted() }


    override fun getResultWithMonthlyPayment(monthlyPayment: Int): Double {
        clear()
        doFirstPayment(monthlyPayment)
        for (dayIndex in 1..trimmedAssetFund.size - 1) {
            moveFundsIfNeeded(dayIndex)
            doPaymentIfNeeded(monthlyPayment)
        }
        return 0.0
    }

    override fun getResultWithCustomPayment(monthlyPayment: Int): Double {
        throw UnsupportedOperationException("not implemented")
    }

    override fun clear() {
        moneyInSafeAsset = false
        inpayments.clear()
    }

    private fun doFirstPayment(monthlyPayment: Int) {
        var valueEntry = trimmedAssetFund.first()
        inpayments.put(valueEntry.date, Inpayment(valueEntry.date, monthlyPayment,
                monthlyPayment / valueEntry.value, assetFund))
    }

    private fun moveFundsIfNeeded(dayIndex: Int) {
        if (!moneyInSafeAsset) {
            val todaysValue = trimmedAssetFund[dayIndex]
            if (rescueNeeded(todaysValue)) {
                moneyInSafeAsset = true
                rescueFunds()
            }
        } else {
            val todaysValue = trimmedSafeAssetFund[dayIndex]
            if (canPutMoneyBack(todaysValue)) {
                moneyInSafeAsset = false
                moveUpFunds()
            }
        }
    }

    private fun rescueNeeded(todaysValue: ValueEntry): Boolean
            = todaysValue.value < previousOneMonthAverage(todaysValue, assetFund) * 0.95

    private fun canPutMoneyBack(todaysValue: ValueEntry): Boolean
            = todaysValue.value > previousOneMonthAverage(todaysValue, assetFund) * 1.05

    private fun rescueFunds() {

    }

    private fun moveUpFunds() {

    }

    private fun doPaymentIfNeeded(monthlyPayment: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    private fun previousOneMonthAverage(todayValue: ValueEntry, checkedAssetFund: AssetFund): Double {
        val endIndex = checkedAssetFund.valueHistory.indexOf(todayValue)
        val startIndex = if (endIndex - 30 < 0) {
            0
        } else {
            endIndex - 30
        }
        val divider = endIndex - startIndex
        return trimmedAssetFund
                .filterIndexed { i, valueEntry -> i >= startIndex && i <= endIndex }
                .sumByDouble { it.value } / divider
    }
}