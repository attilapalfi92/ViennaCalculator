package com.attilapalfi.tools.viennacalculator.logic.validation

import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator

/**
 * Created by 212461305 on 2016.02.22..
 */
interface AssetFundValidator {
    fun getValidAssetFundCalculator(): AssetFundCalculator
    fun validate(): ValidationResult
}