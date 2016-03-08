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
class PriceMonitoringFundCalculator(assetFund: AssetFund, safeAssetFund: AssetFund,
                                    payStartDate: LocalDate, payEndDate: LocalDate,
                                    buybackDate: LocalDate, autoPriceMonitoring: Boolean,
                                    monthlyPayment: Int, customPayments: Map<LocalDate, Int>) : AssetFundCalculator,
        AbstractAssetFundWorker(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate,
                autoPriceMonitoring, monthlyPayment, customPayments) {

    private val inpaymentDays: Int = if (payStartDate.dayOfMonth > 28) {
        28
    } else {
        payStartDate.dayOfMonth
    }
    val payMonthQuarter: Int = (inpaymentDays - 1) / 7;
    private var moneyInSafeAsset: Boolean = false
    private val inpayments = TreeMap<LocalDate, Inpayment>()

    private val trimmedAssetFund: List<ValueEntry>
            by lazy {
                if (autoPriceMonitoring) {
                    assetFund.valueHistory.filter {
                        it.date >= payStartDate && it.date <= buybackDate
                                && Collections.binarySearch<LocalDate>(
                                safeAssetFund.valueHistory.map { it.date }, it.date) >= 0
                    }.sorted()
                } else {
                    assetFund.valueHistory.filter { it.date >= payStartDate && it.date <= buybackDate }.sorted()
                }
            }

    private val trimmedSafeAssetFund: List<ValueEntry>
            by lazy {
                safeAssetFund.valueHistory.filter {
                    it.date >= payStartDate && it.date <= buybackDate
                            && Collections.binarySearch<LocalDate>(
                            assetFund.valueHistory.map { it.date }, it.date) >= 0
                }.sorted()
            }

    private var todaysValue: ValueEntry = ValueEntry(LocalDate.MIN, Double.MIN_VALUE)
    private var todaysSafeValue: ValueEntry = ValueEntry(LocalDate.MIN, Double.MIN_VALUE)
    private var today: LocalDate = payStartDate

    private var payedThisMonth: Boolean = false;
    private var previousMonth: Int = 0
    private var previousYear: Int = 0

    override fun getBuybackCalculator(): BuybackCalculator {
        clear()
        //doFirstInpayment()
        trimmedAssetFund.forEachIndexed { i, valueEntry ->
            today = valueEntry.date
            simulateDay(i)
        }
        // TODO: weekends are not in the list!!!!!!!! HANDLE SHIIIIT! AAAAAARGGHH
        return BuybackCalculatorImpl(inpayments, buybackDate, todaysValue, todaysSafeValue, moneyInSafeAsset)
    }

    private fun simulateDay(dayIndex: Int) {
        todaysValue = trimmedAssetFund[dayIndex]
        if (autoPriceMonitoring) {
            todaysSafeValue = trimmedSafeAssetFund[dayIndex]
            moveFundsIfNeeded()
        }
        doInpaymentIfNeeded()
    }

    override fun clear() {
        moneyInSafeAsset = false
        today = payStartDate
        inpayments.clear()
    }

    private fun doFirstInpayment() {
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

    private fun doInpaymentIfNeeded() {
        if (today.year > previousYear) {
            previousMonth = 0
            previousYear = today.year
        }
        if (today.monthValue > previousMonth) {
            payedThisMonth = false
            previousMonth = today.monthValue
        }
        if ((today.dayOfMonth - 1) / 7 == payMonthQuarter) {
            if (!payedThisMonth) {
                doActualPayment()
                payedThisMonth = true
            }
        }
    }

    private fun doActualPayment() {
        if (!moneyInSafeAsset) {
            inpayments.put(today, Inpayment(today, monthlyPayment,
                    monthlyPayment / todaysValue.value, assetFund))
        } else {
            inpayments.put(today, Inpayment(today, monthlyPayment,
                    monthlyPayment / todaysSafeValue.value, safeAssetFund))
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