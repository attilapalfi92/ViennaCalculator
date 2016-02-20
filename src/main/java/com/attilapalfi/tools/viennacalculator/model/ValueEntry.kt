package com.attilapalfi.tools.viennacalculator.model

/**
 * Created by palfi on 2016-02-20.
 */
class ValueEntry(val epochSeconds: Long, val value: Double) : Comparable<ValueEntry> {

    override fun compareTo(other: ValueEntry): Int {
        return epochSeconds.compareTo(other.epochSeconds)
    }

}