package com.attilapalfi.tools.viennacalculator.view

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.scene.control.*

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
                     var removeButton: Button? = null)