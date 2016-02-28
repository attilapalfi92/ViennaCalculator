package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.logic.implementations.AssetFundValidatorImpl
import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import org.junit.Before
import java.time.LocalDate

/**
 * Created by palfi on 2016-02-28.
 */
abstract class AbstractLogicTest {

    protected lateinit var assetFundCalculator: AssetFundCalculator

    protected val assetFundName = "test asset fund"
    protected val safeAssetFundName = "safe test asset fund"

    protected abstract val startDate: LocalDate
    protected abstract val endDate: LocalDate

    protected abstract val payStartDate: LocalDate
    protected abstract val payEndDate: LocalDate
    protected abstract val buybackDate: LocalDate
}