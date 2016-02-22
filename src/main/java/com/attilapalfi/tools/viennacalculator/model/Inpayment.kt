package com.attilapalfi.tools.viennacalculator.model

import java.time.LocalDate

/**
 * Created by palfi on 2016-02-22.
 */
class Inpayment(val paymentDate: LocalDate, val originalForintAmount: Int,
                val bondAmount: Double, val assetFund: AssetFund) : Comparable<Inpayment> {

    override fun compareTo(other: Inpayment): Int {
        return paymentDate.compareTo(other.paymentDate)
    }
}