package com.attilapalfi.tools.viennacalculator.logic.validation

import com.attilapalfi.tools.viennacalculator.logic.AbstractAssetFundWorker
import com.attilapalfi.tools.viennacalculator.logic.AssetFundCalculator
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import sun.plugin.dom.exception.InvalidStateException
import java.time.LocalDate

/**
 * Created by palfi on 2016-02-21.
 */
class InputValidator(assetFund: AssetFund, safeAssetFund: AssetFund,
                     payStartDate: LocalDate, payEndDate: LocalDate,
                     buybackDate: LocalDate) :
        AbstractAssetFundWorker(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate) {

    private var lastValidationResult: ValidationResult = ValidationResult.UNVALIDATED

    fun getYieldCalculator(): AssetFundCalculator {
        if (lastValidationResult == ValidationResult.UNVALIDATED) {
            validate()
        }
        if (lastValidationResult != ValidationResult.VALID) {
            throw InvalidStateException(lastValidationResult.name)
        }
        return AssetFundCalculator(assetFund, safeAssetFund, payStartDate, payEndDate, buybackDate)
    }

    fun validate(): ValidationResult {
        lastValidationResult = validateDatesToEachOther()
        if (lastValidationResult != ValidationResult.VALID) {
            return lastValidationResult
        }
        lastValidationResult = validateStartDate()
        if (lastValidationResult != ValidationResult.VALID) {
            return lastValidationResult
        }
        lastValidationResult = validateEndDate()
        if (lastValidationResult != ValidationResult.VALID) {
            return lastValidationResult
        }
        lastValidationResult = validateBuybackDate()
        if (lastValidationResult != ValidationResult.VALID) {
            return lastValidationResult
        }
        lastValidationResult = ValidationResult.VALID
        return ValidationResult.VALID
    }

    private fun validateDatesToEachOther(): ValidationResult {
        if (payStartDate > payEndDate) {
            return ValidationResult.START_LATER_THAN_END
        }
        if (payStartDate > buybackDate) {
            return ValidationResult.START_LATER_THAN_BUYBACK
        }
        if (payEndDate > buybackDate) {
            return ValidationResult.END_LATER_THAN_BUYBACK
        }
        return ValidationResult.VALID
    }

    private fun validateStartDate(): ValidationResult {
        if (payStartDate < assetFund.valueHistory.first().date
                || payStartDate < safeAssetFund.valueHistory.first().date
                || payStartDate > assetFund.valueHistory.last().date
                || payStartDate > safeAssetFund.valueHistory.last().date) {

            return ValidationResult.OUT_OF_RANGE_PAY_START_DATE
        }
        return ValidationResult.VALID
    }

    private fun validateEndDate(): ValidationResult {
        if (payEndDate < assetFund.valueHistory.first().date
                || payEndDate < safeAssetFund.valueHistory.first().date
                || payEndDate > assetFund.valueHistory.last().date
                || payEndDate > safeAssetFund.valueHistory.last().date) {

            return ValidationResult.OUT_OF_RANGE_PAY_END_DATE
        }
        return ValidationResult.VALID
    }

    private fun validateBuybackDate(): ValidationResult {
        if (buybackDate < assetFund.valueHistory.first().date
                || buybackDate < safeAssetFund.valueHistory.first().date
                || buybackDate > assetFund.valueHistory.last().date
                || buybackDate > safeAssetFund.valueHistory.last().date) {

            return ValidationResult.OUT_OF_RANGE_BUYBACK_DATE
        }
        return ValidationResult.VALID
    }
}