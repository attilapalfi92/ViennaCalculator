package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import java.time.LocalDate

/**
 * Created by palfi on 2016-02-21.
 */
abstract class AbstractAssetFundWorker(val assetFund: AssetFund, val safeAssetFund: AssetFund,
                                       val payStartDate: LocalDate, val payEndDate: LocalDate,
                                       val buybackDate: LocalDate, val autoPriceMonitoring: Boolean,
                                       val monthlyPayment: Int, val customPayments: Map<LocalDate, Int>) {
}