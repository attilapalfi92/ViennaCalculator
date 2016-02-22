package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import java.time.LocalDate

/**
 * Created by palfi on 2016-02-21.
 */
abstract class AbstractAssetFundWorker(protected val assetFund: AssetFund, protected val safeAssetFund: AssetFund,
                                       protected val payStartDate: LocalDate, protected val payEndDate: LocalDate,
                                       protected val buybackDate: LocalDate) {
}