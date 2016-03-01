package com.attilapalfi.tools.viennacalculator.model

import com.attilapalfi.tools.viennacalculator.logic.implementations.AssetFundValidatorImpl
import com.attilapalfi.tools.viennacalculator.logic.validation.AssetFundValidator
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import java.time.LocalDate

/**
 * Created by 212461305 on 2016.02.29..
 */
data class FundViewDataHolder(val paymentStart: LocalDate,
                              val paymentEnd: LocalDate,
                              val assetFund: AssetFund,
                              val monthlyPayment: Int = 0,
                              val autoPriceMonitoring: Boolean = false,
                              val customPayments: Map<LocalDate, Int> = emptyMap()) {

    fun getValidator(safeAssetFund: AssetFund, buybackDate: LocalDate): AssetFundValidator {
        return AssetFundValidatorImpl(
                assetFund, safeAssetFund, paymentStart, paymentEnd, buybackDate, autoPriceMonitoring
        )
    }

}