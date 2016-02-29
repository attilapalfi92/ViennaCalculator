package com.attilapalfi.tools.viennacalculator.view

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.scene.control.*
import sun.plugin.dom.exception.InvalidStateException

/**
 * Created by 212461305 on 2016.02.29..
 */
class FundViewHolder(var paymentStartDate: DatePicker? = null,
                     var paymentEndDate: DatePicker? = null,
                     var monthlyPaymentText: TextField? = null,
                     var assetFundChoiceBox: ChoiceBox<AssetFund>? = null,
                     var paymentRateMonitoringCheckBox: CheckBox? = null,
                     var customPaymentsButton: Button? = null,
                     var individualResultsButton: Button? = null,
                     var removeButton: Button? = null) {

    fun dataIsFilled(): Boolean {
        if (paymentStartDate?.value != null
                && paymentEndDate?.value != null
                && !monthlyPaymentText?.text.isNullOrBlank()
                && assetFundChoiceBox?.value != null) {
            return true
        }
        return false
    }

    /**
     * Throws NumberFormatException and InvalidStateException.
     * NumberFormatException must be handled.
     */
    fun getDataHolder(): FundViewDataHolder {
        if (!dataIsFilled()) {
            throw InvalidStateException("data is not filled")
        }

        return FundViewDataHolder(
                paymentStart = paymentStartDate?.value ?:
                        throw InvalidStateException("paymentStartDate shouldn't be null"),
                paymentEnd = paymentEndDate?.value ?:
                        throw InvalidStateException("paymentEndDate shouldn't be null"),
                assetFund = assetFundChoiceBox?.value ?:
                        throw InvalidStateException("assetFundChoiceBox shouldn't be null"),
                monthlyPayment = monthlyPaymentText?.text?.toInt() ?:
                        throw InvalidStateException("monthlyPaymentText shouldn't be null"),
                autoPriceMonitoring = paymentRateMonitoringCheckBox?.selectedProperty()?.value ?:
                        throw InvalidStateException("paymentRateMonitoringCheckBox shouldn't be null")
        )
    }

}