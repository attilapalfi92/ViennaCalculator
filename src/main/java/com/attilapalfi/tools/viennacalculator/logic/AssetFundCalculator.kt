package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.Inpayment
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import java.time.LocalDate
import java.util.*

/**
 * Created by palfi on 2016-02-21.
 */
class AssetFundCalculator(assetFund: AssetFund, safeAssetFund: AssetFund,
                          payStartDate: LocalDate, payEndDate: LocalDate,
                          buybackDate: LocalDate) :
        AbstractAssetFundWorker(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate) {

    private val purchaseDays: Int = if (payStartDate.dayOfMonth > 29) { 29 } else { payStartDate.dayOfMonth }
    private var moneyInSafeAsset: Boolean = false
    private val inpayments = TreeSet<Inpayment>()

    private val trimmedAssetFund: Set<ValueEntry>
            by lazy { assetFund.valueHistory.filter { it.date >= payStartDate || it.date <= payEndDate }.toSortedSet() }

    private val trimmedSafeAssetFund: Set<ValueEntry>
            by lazy { safeAssetFund.valueHistory.filter { it.date >= payStartDate || it.date <= payEndDate }.toSortedSet() }

    fun getResultWithMonthlyPayment(monthlyPayment: Int): Double {
        for (i in 0..trimmedAssetFund.size - 1) {
            /*if (haveToSaveFound(i)) {

            } else {

            }*/
        }

        return 0.0
    }


    private fun previousOneMonthAverage(endIndex: Int): Double {
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