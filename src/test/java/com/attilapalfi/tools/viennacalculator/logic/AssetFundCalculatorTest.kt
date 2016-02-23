package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.TestDiagramVisualizer
import com.attilapalfi.tools.viennacalculator.logic.implementations.AssetFundValidatorImpl
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.*

/**
 * Created by palfi on 2016-02-22.
 */
class AssetFundCalculatorTest {

    private val assetFundName = "test asset fund"
    private val safeAssetFundName = "safe test asset fund"

    private lateinit var assetFundCalculator: AssetFundCalculator

    val startDate = LocalDate.of(2000, 1, 1)
    val endDate = LocalDate.of(2015, 12, 31)

    val payStartDate = LocalDate.of(2000, 1, 1)
    val payEndDate = LocalDate.of(2005, 1, 1)
    val buybackDate = LocalDate.of(2010, 1, 1)

    var diagramThread1: Thread = Thread()
    var diagramThread2: Thread = Thread()

    @Before
    fun setup() {
        val holder = getAssetFundHolder()
        assetFundCalculator = AssetFundValidatorImpl(holder.assetFunds.first(),
                holder.safeAssetFund, payStartDate, payEndDate, buybackDate).getValidAssetFundCalculator()
    }


    @Test
    fun testCalculator() {
        val buybackCalculator = assetFundCalculator.getResultWithMonthlyPayment(100000)
        Assert.assertNotNull(buybackCalculator)
        diagramThread1.join()
        diagramThread2.join()
    }


    fun getAssetFundHolder(): AssetFoundHolder {
        val valueHistory = TreeSet<ValueEntry>().apply { add(ValueEntry(startDate, 1.0)) }
        val safeValueHistory = TreeSet<ValueEntry>().apply { add(ValueEntry(startDate, 1.0)) }

        loadHistory(safeValueHistory, valueHistory)
        startThreads(safeValueHistory, valueHistory)

        val assetFund = AssetFund(assetFundName, valueHistory)
        val safeAssetFund = AssetFund(safeAssetFundName, safeValueHistory)
        return AssetFoundHolder(listOf(assetFund), safeAssetFund)
    }

    private fun loadHistory(safeValueHistory: TreeSet<ValueEntry>, valueHistory: TreeSet<ValueEntry>) {
        for (i in 1..endDate.toEpochDay() - startDate.toEpochDay()) {
            val currentDay = startDate.plusDays(i)
            if (currentDay.year % 3 == 0) {
                valueHistory.add(ValueEntry(currentDay, valueHistory.last().value - 0.0001))
            } else if (currentDay.year % 5 == 0) {
                valueHistory.add(ValueEntry(currentDay, valueHistory.last().value - 0.0003))
            } else {
                valueHistory.add(ValueEntry(currentDay, valueHistory.last().value + 0.0004))
            }
            safeValueHistory.add(ValueEntry(currentDay, safeValueHistory.last().value + 0.00005))
        }
    }

    private fun startThreads(safeValueHistory: TreeSet<ValueEntry>, valueHistory: TreeSet<ValueEntry>) {
        diagramThread1 = Thread({ TestDiagramVisualizer("Value History", valueHistory).showChart(1) }).apply { start() }
        diagramThread2 = Thread({ TestDiagramVisualizer("Safe Value History", safeValueHistory).showChart(1) }).apply { start() }
    }

}