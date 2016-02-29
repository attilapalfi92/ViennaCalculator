package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.add
import tornadofx.addTo

/**
 * Created by palfi on 2016-02-28.
 */
class FundViewBuilder(val fundContainer: VBox)  {

    private val fundViewHolder = FundViewHolder()

    fun build(): FundViewHolder {
        val titledPane = getTitledPane()
        fundContainer.add(titledPane)
        val vBox = getVBox(titledPane)
        addHBoxToVBox(vBox)
        addMonthlyPaymentText(vBox)
        addLabelAndChoiceBox(vBox)
        addCheckBox(vBox)
        addResultsButton(vBox)
        addRemoveButton(vBox)

        return fundViewHolder
    }

    private fun getTitledPane(): TitledPane {
        val titledPane = TitledPane()
        titledPane.text = "Eszközalap"
        titledPane.maxWidth = Double.MAX_VALUE
        titledPane.maxHeight = Double.MAX_VALUE
        return titledPane
    }

    private fun getVBox(titledPane: TitledPane): VBox {
        val vBox = VBox()
        vBox.maxWidth = Double.MAX_VALUE
        vBox.maxHeight = Double.MAX_VALUE
        vBox.spacing = 5.0
        titledPane.content = vBox
        return vBox
    }

    private fun addHBoxToVBox(vBox: VBox): HBox {
        val hBox = HBox()
        hBox.spacing = 5.0
        addPaymentStart(hBox)
        addPaymentEnd(hBox)
        hBox.addTo(vBox)
        return hBox
    }

    private fun addPaymentStart(hBox: HBox) {
        val paymentStart = DatePicker()
        paymentStart.promptText = "Befizetés kezdete"
        fundViewHolder.paymentStartDate = paymentStart
        hBox.add(paymentStart)
    }

    private fun addPaymentEnd(hBox: HBox) {
        val paymentEnd = DatePicker()
        paymentEnd.promptText = "Befizetés vége"
        fundViewHolder.paymentEndDate = paymentEnd
        hBox.add(paymentEnd)
    }

    private fun addMonthlyPaymentText(vBox: VBox) {
        val textField = TextField()
        textField.promptText = "Havi befizetés"
        fundViewHolder.monthlyPaymentText = textField
        vBox.add(textField)
    }

    private fun addLabelAndChoiceBox(vBox: VBox) {
        val label = Label("Eszközalap választás")
        vBox.add(label)
        val choiceBox = ChoiceBox<AssetFund>()
        fundViewHolder.assetFundChoiceBox = choiceBox
        vBox.add(choiceBox)
    }

    private fun addCheckBox(vBox: VBox) {
        val checkBox = CheckBox("Automatikus árfolyamfigyelés")
        fundViewHolder.paymentRateMonitoringCheckBox = checkBox
        vBox.add(checkBox)
    }

    private fun addResultsButton(vBox: VBox) {
        val resultsButton = Button("Egyedi eredmények megtekintése")
        resultsButton.isDisable = true
        fundViewHolder.individualResultsButton = resultsButton
        vBox.add(resultsButton)
    }

    private fun addRemoveButton(vBox: VBox) {
        val removeButton = Button("Eltávolítás")
        fundViewHolder.removeButton = removeButton
        vBox.add(removeButton)
    }
}