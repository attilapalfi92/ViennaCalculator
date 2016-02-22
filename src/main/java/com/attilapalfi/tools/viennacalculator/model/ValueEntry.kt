package com.attilapalfi.tools.viennacalculator.model

import java.time.LocalDate

/**
 * Created by palfi on 2016-02-20.
 */
class ValueEntry(val date: LocalDate, val value: Double) : Comparable<ValueEntry> {

    override fun compareTo(other: ValueEntry): Int {
        return date.compareTo(other.date)
    }

}