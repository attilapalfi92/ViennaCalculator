package com.attilapalfi.tools.viennacalculator.view

import com.attilapalfi.tools.viennacalculator.model.AssetFund
import javafx.scene.control.ChoiceBox
import javafx.scene.control.DateCell
import java.time.LocalDate

/**
 * Created by palfi on 2016-03-05.
 */
class RestrictingDateCell( private val choiceBox: ChoiceBox<AssetFund> ) : DateCell() {

    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        item?.let {
            if (it.isBefore(choiceBox.value?.valueHistory?.first()?.date) ||
                    it.isAfter(choiceBox.value?.valueHistory?.last()?.date)) {
                style = "-fx-background-color: #ffc0cb;"
                isDisable = true
            }
        }
    }
}