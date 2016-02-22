package com.attilapalfi.tools.viennacalculator.logic.validation

/**
 * Created by palfi on 2016-02-21.
 */
enum class ValidationResult {
    VALID,
    OUT_OF_RANGE_PAY_START_DATE,
    OUT_OF_RANGE_PAY_END_DATE,
    OUT_OF_RANGE_BUYBACK_DATE,
    START_LATER_THAN_END,
    START_LATER_THAN_BUYBACK,
    END_LATER_THAN_BUYBACK,
    UNVALIDATED
}