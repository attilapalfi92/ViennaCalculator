package com.attilapalfi.tools.viennacalculator.model

/**
 * Created by palfi on 2016-02-20.
 */
data class AssetFund(val name: String, val valueHistory: Set<ValueEntry>) {
    override fun toString(): String = name
}