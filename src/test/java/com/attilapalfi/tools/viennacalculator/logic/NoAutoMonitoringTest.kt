package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.logic.implementations.AssetFundValidatorImpl
import com.attilapalfi.tools.viennacalculator.model.AssetFundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.*

/**
 * Created by palfi on 2016-02-28.
 */
class NoAutoMonitoringTest : AbstractLogicTest() {

    override val startDate = LocalDate.of(1995, 1, 1)
    override val endDate: LocalDate = LocalDate.of(2010, 1, 2)

    override val payStartDate = LocalDate.of(2000, 1, 1)
    override val payEndDate = LocalDate.of(2003, 1, 1)
    override val buybackDate = LocalDate.of(2003, 1, 1)

    @Test
    fun testWithIncreasingFund() {
        val holder = getIncreasingAssetFundHolder()
        assetFundCalculator = AssetFundValidatorImpl(holder.assetFunds.first(),
                holder.safeAssetFund, payStartDate, payEndDate, buybackDate, false, 10000, emptyMap())
                .getValidAssetFundCalculator()

        val buybackCalculator = assetFundCalculator.getBuybackCalculator()
        Assert.assertNotNull(buybackCalculator)
        val outcome = buybackCalculator.getInvestmentOutcome()
        print(outcome)
        Assert.assertEquals(3600, outcome.totalBuybackFeeInForints)
        Assert.assertEquals(36000, outcome.totalInpayedForints)
    }

    @Test
    fun testWithZeroingFund() {
        val holder = getZeroingAssetFundHolder()
        assetFundCalculator = AssetFundValidatorImpl(holder.assetFunds.first(),
                holder.safeAssetFund, payStartDate, payEndDate, buybackDate, false, 10000, emptyMap())
                .getValidAssetFundCalculator()

        val buybackCalculator = assetFundCalculator.getBuybackCalculator()
        Assert.assertNotNull(buybackCalculator)
        val outcome = buybackCalculator.getInvestmentOutcome()
        print(outcome)
        Assert.assertEquals(-3600, outcome.totalBuybackFeeInForints)
        Assert.assertEquals(36000, outcome.totalInpayedForints)
    }

    private fun getZeroingAssetFundHolder(): AssetFundHolder {
        val (valueHistory, safeValueHistory) = getInitialHistory()
        loadZeroingHistory(safeValueHistory, valueHistory)
        return assetFoundHolder(safeValueHistory, valueHistory)
    }

    private fun getInitialHistory(): Pair<TreeSet<ValueEntry>, TreeSet<ValueEntry>> {
        val valueHistory = TreeSet<ValueEntry>().apply { add(ValueEntry(startDate, 1.0)) }
        val safeValueHistory = TreeSet<ValueEntry>().apply { add(ValueEntry(startDate, 1.0)) }
        return Pair(valueHistory, safeValueHistory)
    }

    private fun loadZeroingHistory(safeValueHistory: TreeSet<ValueEntry>, valueHistory: TreeSet<ValueEntry>) {
        for (i in 1..endDate.toEpochDay() - startDate.toEpochDay()) {
            val currentDay = startDate.plusDays(i)
            valueHistory.add(ValueEntry(currentDay, valueHistory.last().value))
            safeValueHistory.add(ValueEntry(currentDay, safeValueHistory.last().value))
        }
    }

    private fun assetFoundHolder(safeValueHistory: TreeSet<ValueEntry>, valueHistory: TreeSet<ValueEntry>): AssetFundHolder {
        val assetFund = AssetFund(assetFundName, valueHistory)
        val safeAssetFund = AssetFund(safeAssetFundName, safeValueHistory)
        return AssetFundHolder(listOf(assetFund), safeAssetFund)
    }

    private fun getIncreasingAssetFundHolder(): AssetFundHolder {
        val (valueHistory, safeValueHistory) = getInitialHistory()
        loadIncreasingHistory(safeValueHistory, valueHistory)
        return assetFoundHolder(safeValueHistory, valueHistory)
    }

    private fun loadIncreasingHistory(safeValueHistory: TreeSet<ValueEntry>, valueHistory: TreeSet<ValueEntry>) {
        for (i in 1..endDate.toEpochDay() - startDate.toEpochDay()) {
            val currentDay = startDate.plusDays(i)
            valueHistory.add(ValueEntry(currentDay, valueHistory.last().value + 0.0001))
            safeValueHistory.add(ValueEntry(currentDay, safeValueHistory.last().value + 0.00005))
        }
    }
}