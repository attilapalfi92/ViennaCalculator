package com.attilapalfi.tools.viennacalculator.logic.implementations

import com.attilapalfi.tools.viennacalculator.logic.AbstractAssetFundWorker
import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.logic.BuybackCalculator
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.Inpayment
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import java.time.LocalDate
import java.util.*

/**
 * Created by 212461305 on 2016.02.23..
 */
class AssetFundCalculatorImpl(assetFund: AssetFund, safeAssetFund: AssetFund,
                              payStartDate: LocalDate, payEndDate: LocalDate,
                              buybackDate: LocalDate, autoPriceMonitoring: Boolean) : AssetFundCalculator,
        AbstractAssetFundWorker(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate, autoPriceMonitoring) {

    private val inpaymentDays: Int = if (payStartDate.dayOfMonth > 28) {
        28
    } else {
        payStartDate.dayOfMonth
    }
    private var moneyInSafeAsset: Boolean = false
    private val inpayments = TreeMap<LocalDate, Inpayment>()

    private val trimmedAssetFund: List<ValueEntry>
            by lazy { assetFund.valueHistory.filter { it.date >= payStartDate && it.date <= buybackDate }.sorted() }

    private val trimmedSafeAssetFund: List<ValueEntry>
            by lazy { safeAssetFund.valueHistory.filter { it.date >= payStartDate && it.date <= buybackDate }.sorted() }

    private var todaysValue: ValueEntry = ValueEntry(LocalDate.MIN, Double.MIN_VALUE)
    private var todaysSafeValue: ValueEntry = ValueEntry(LocalDate.MIN, Double.MIN_VALUE)
    private var today: LocalDate = payStartDate

    override fun getResultWithMonthlyPayment(monthlyPayment: Int): BuybackCalculator {
        clear()
        doFirstInpayment(monthlyPayment)
        for (dayIndex in 1..trimmedAssetFund.size - 1) {
            simulateDay(dayIndex, monthlyPayment)
        }
        return BuybackCalculatorImpl(inpayments, buybackDate, todaysValue, todaysSafeValue, moneyInSafeAsset)
    }

    private fun simulateDay(dayIndex: Int, monthlyPayment: Int) {
        today = today.plusDays(1)
        todaysValue = trimmedAssetFund[dayIndex]
        if (autoPriceMonitoring) {
            todaysSafeValue = trimmedSafeAssetFund[dayIndex]
            moveFundsIfNeeded()
        }
        doInpaymentIfNeeded(monthlyPayment)
    }

    override fun getResultWithCustomPayment(monthlyPayment: Int): BuybackCalculator {
        throw UnsupportedOperationException("not implemented")
    }

    override fun clear() {
        moneyInSafeAsset = false
        today = payStartDate
        inpayments.clear()
    }

    private fun doFirstInpayment(monthlyPayment: Int) {
        var valueEntry = trimmedAssetFund.first()
        inpayments.put(valueEntry.date, Inpayment(valueEntry.date, monthlyPayment,
                monthlyPayment / valueEntry.value, assetFund))
    }

    private fun moveFundsIfNeeded() {
        if (!moneyInSafeAsset) {
            rescueFundsIfNeeded()
        } else {
            moveUpFundsIfNeeded()
        }
    }

    private fun rescueFundsIfNeeded() {
        if (rescueNeeded()) {
            moneyInSafeAsset = true
            rescueFunds()
        }
    }

    private fun moveUpFundsIfNeeded() {
        if (canMoveUpFunds()) {
            moneyInSafeAsset = false
            moveUpFunds()
        }
    }

    private fun rescueNeeded(): Boolean
            = todaysValue.value < previousOneMonthAverage(todaysValue) * 0.95

    private fun canMoveUpFunds(): Boolean
            = todaysValue.value > previousOneMonthAverage(todaysValue) * 1.05

    private fun rescueFunds() {
        inpayments.forEach { inpayDate, inpayment ->
            val currentForintAmount = inpayment.bondAmount * todaysValue.value
            inpayment.bondAmount = currentForintAmount / todaysSafeValue.value
            inpayment.assetFund = safeAssetFund
        }
    }

    private fun moveUpFunds() {
        inpayments.forEach { inpayDate, inpayment ->
            val currentForintAmount = inpayment.bondAmount * todaysSafeValue.value
            inpayment.bondAmount = currentForintAmount / todaysValue.value
            inpayment.assetFund = assetFund
        }
    }

    private fun doInpaymentIfNeeded(monthlyPayment: Int) {
        if (today < payEndDate) {
            if (today.dayOfMonth == inpaymentDays) {
                if (!moneyInSafeAsset) {
                    inpayments.put(today, Inpayment(today, monthlyPayment,
                            monthlyPayment / todaysValue.value, assetFund))
                } else {
                    inpayments.put(today, Inpayment(today, monthlyPayment,
                            monthlyPayment / todaysSafeValue.value, safeAssetFund))
                }
            }
        }
    }

    private fun previousOneMonthAverage(todayValue: ValueEntry): Double {
        val endIndex = assetFund.valueHistory.indexOf(todayValue)
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