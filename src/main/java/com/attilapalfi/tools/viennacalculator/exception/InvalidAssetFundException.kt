package com.attilapalfi.tools.viennacalculator.exception

import com.attilapalfi.tools.viennacalculator.logic.validation.ValidationResult

/**
 * Created by 212461305 on 2016.02.22..
 */
class InvalidAssetFundException(val validationResult: ValidationResult) : Exception()