package com.attilapalfi.tools.viennacalculator.view

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
                              val customPayments: Map<LocalDate, Int> = emptyMap())