package com.attilapalfi.tools.viennacalculator.controller

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.AssetFundHolder
import com.attilapalfi.tools.viennacalculator.view.AssetFundChartView
import com.attilapalfi.tools.viennacalculator.view.FundViewHolder
import com.attilapalfi.tools.viennacalculator.view.RestrictingDateCell
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.Callback
import tornadofx.add

/**
 * Created by palfi on 2016-02-28.
 */
class FundViewBuilder(val fundContainer: VBox) {

    private val fundViewHolder = FundViewHolder()

    fun build(assetFundHolder: AssetFundHolder?): FundViewHolder {
        val titledPane = getTitledPane()
        fundContainer.add(titledPane)
        val vBox = getVBox(titledPane)
        val choiceBox = ChoiceBox<AssetFund>()
        addHBoxToVBox(vBox, choiceBox)
        addMonthlyPaymentText(vBox)
        addLabelAndChoiceBox(choiceBox, vBox, assetFundHolder)
        addDiagramButton(vBox, assetFundHolder, choiceBox)
        addCheckBox(vBox)
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

    private fun addHBoxToVBox(vBox: VBox, choiceBox: ChoiceBox<AssetFund>): HBox {
        val hBox = HBox()
        hBox.spacing = 5.0
        addPaymentStart(hBox, choiceBox)
        addPaymentEnd(hBox, choiceBox)
        //        hBox.addTo(vBox)
        //        vBox.add(hBox)
        vBox.children.add(hBox)
        return hBox
    }

    private fun addPaymentStart(hBox: HBox, choiceBox: ChoiceBox<AssetFund>) {
        val paymentStart = DatePicker()
        paymentStart.dayCellFactory = Callback {
            RestrictingDateCell(choiceBox)
        }
        paymentStart.promptText = "Befizetés kezdete"
        fundViewHolder.paymentStartDate = paymentStart
        hBox.add(paymentStart)
    }

    private fun addPaymentEnd(hBox: HBox, choiceBox: ChoiceBox<AssetFund>) {
        val paymentEnd = DatePicker()
        paymentEnd.dayCellFactory = Callback {
            RestrictingDateCell(choiceBox)
        }
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

    private fun addLabelAndChoiceBox(choiceBox: ChoiceBox<AssetFund>, vBox: VBox, assetFundHolder: AssetFundHolder?) {
        val label = Label("Eszközalap választás")
        vBox.add(label)
        assetFundHolder?.let {
            choiceBox.items.addAll(it.assetFunds)
            choiceBox.selectionModel.select(0)
        }
        fundViewHolder.assetFundChoiceBox = choiceBox
        vBox.add(choiceBox)
    }

    private fun addDiagramButton(vBox: VBox, assetFundHolder: AssetFundHolder?, choiceBox: ChoiceBox<AssetFund>) {
        val diagramButton = Button("Diagram megtekintése")
        diagramButton.isDisable = assetFundHolder == null
        diagramButton.onAction = EventHandler {
            if ( choiceBox.value != null ) {
                AssetFundChartView.show(choiceBox.value)
            } else {
                // TODO: handle shit
            }
        }
        fundViewHolder.showDiagramButton = diagramButton
        vBox.add(diagramButton)
    }

    private fun addCheckBox(vBox: VBox) {
        val checkBox = CheckBox("Automatikus árfolyamfigyelés")
        fundViewHolder.paymentRateMonitoringCheckBox = checkBox
        vBox.add(checkBox)
    }

    private fun addRemoveButton(vBox: VBox) {
        val removeButton = Button("Eltávolítás")
        fundViewHolder.removeButton = removeButton
        vBox.add(removeButton)
    }
}