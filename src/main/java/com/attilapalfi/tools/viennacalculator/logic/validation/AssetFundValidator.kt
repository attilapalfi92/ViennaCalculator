package com.attilapalfi.tools.viennacalculator.logic.validation

import com.attilapalfi.tools.viennacalculator.logic.AbstractAssetFundWorker
import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import java.time.LocalDate

/**
 * Created by 212461305 on 2016.02.22..
 */
abstract class AssetFundValidator(assetFund: AssetFund, safeAssetFund: AssetFund, payStartDate: LocalDate,
                                  payEndDate: LocalDate, buybackDate: LocalDate, autoPriceMonitoring: Boolean,
                                  monthlyPayment: Int, customPayments: Map<LocalDate, Int>) :
        AbstractAssetFundWorker(assetFund, safeAssetFund, payStartDate, payEndDate,
                buybackDate, autoPriceMonitoring, monthlyPayment, customPayments) {

    abstract fun getValidAssetFundCalculator(): AssetFundCalculator
    abstract fun validate(): ValidationResult
}