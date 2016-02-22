package com.attilapalfi.tools.viennacalculator.model

import java.time.LocalDate

/**
 * Created by palfi on 2016-02-22.
 */
class Inpayment(val inpaymentDate: LocalDate, val originalForintAmount: Int,
                var bondAmount: Double, var assetFund: AssetFund) : Comparable<Inpayment> {

    override fun compareTo(other: Inpayment): Int {
        return inpaymentDate.compareTo(other.inpaymentDate)
    }
}